package dev.mockboard.service;

import dev.mockboard.Constants;
import dev.mockboard.common.domain.dto.BoardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private final Map<String, List<SseEmitter>> webhookEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(BoardDto boardDto) {
        var emitter = new SseEmitter(Constants.SSE_EMITTER_TTL);
        webhookEmitters.compute(boardDto.getApiKey(), (key, emitters) -> {
            var newList = (CollectionUtils.isEmpty(emitters))
                    ? new CopyOnWriteArrayList<SseEmitter>()
                    : emitters;

            if (newList.size() >= Constants.MAX_SSE_EMITTERS_PER_BOARD) {
                try {
                    var oldest = newList.removeFirst();
                    oldest.complete();
                } catch (Exception e) {
                    // should be already dead
                    log.debug(e.getMessage(), e);
                }
            }

            newList.add(emitter);
            return newList;
        });

        emitter.onCompletion(cleanup(boardDto.getApiKey(), emitter));
        emitter.onTimeout(cleanup(boardDto.getApiKey(), emitter));
        emitter.onError(e -> cleanup(boardDto.getApiKey(), emitter).run());
        return emitter;
    }

    private Runnable cleanup(String apiKey, SseEmitter emitter) {
        return () -> webhookEmitters.computeIfPresent(apiKey, (key, list) -> {
            list.remove(emitter);
            return list.isEmpty() ? null : list;
        });
    }

    public void broadcast(String apiKey, Object data) {
        var emitters = webhookEmitters.get(apiKey);
        if (CollectionUtils.isEmpty(emitters)) {
            return;
        }

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(Constants.SSE_EMITTER_EVENT_WEBHOOK)
                        .id(UUID.randomUUID().toString())
                        .data(data, MediaType.APPLICATION_JSON)
                );
            } catch (Exception e) {
                log.debug(e.getMessage(), e);
                emitter.completeWithError(e);
            }
        });
    }

    @Scheduled(fixedRate = Constants.SSE_EMITTER_HEARTBEAT_RATE)
    public void sendHeartbeat() {
        if (webhookEmitters.isEmpty()) {
            return;
        }

        log.trace("Sending heartbeat to {} active boards", webhookEmitters.size());
        webhookEmitters.forEach((key, emitters) -> emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(Constants.SSE_EMITTER_EVENT_PING)
                        .comment("heartbeat")
                );
            } catch (Exception e) {
                emitter.completeWithError(e);
                // probably not needed, spring should handle it
                // cleanup(key, emitter).run();
            }
        }));
    }
}
