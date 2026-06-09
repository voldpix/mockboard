package dev.mockboard.web.sse;

import dev.mockboard.Constants;
import dev.mockboard.common.domain.dto.BoardDto;
import io.javalin.http.sse.SseClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class SseManager {

    private final Map<String, List<SseClient>> webhookClients = new ConcurrentHashMap<>();

    public void subscribe(BoardDto boardDto, SseClient client) {
        webhookClients.compute(boardDto.getId(), (key, clients) -> {
            var newList = (clients == null || clients.isEmpty())
                    ? new CopyOnWriteArrayList<SseClient>()
                    : clients;

            if (!newList.isEmpty()) {
                try {
                    var oldest = newList.removeFirst();
                    oldest.close();
                    log.debug("SSE client size exceeded, oldest removed");
                } catch (Exception e) {
                    log.debug(e.getMessage(), e);
                }
            }

            newList.add(client);
            return newList;
        });

        client.onClose(cleanup(boardDto.getId(), client));
        client.keepAlive();

        try {
            client.sendEvent(Constants.SSE_EMITTER_EVENT_PING, "established");
        } catch (Exception e) {
            closeClient(boardDto.getId(), client, e);
        }
    }

    private Runnable cleanup(String boardId, SseClient client) {
        return () -> webhookClients.computeIfPresent(boardId, (key, list) -> {
            list.remove(client);
            return list.isEmpty() ? null : list;
        });
    }

    public void broadcast(String boardId, Object data) {
        var clients = webhookClients.get(boardId);
        if (clients == null || clients.isEmpty()) {
            return;
        }

        clients.forEach(client -> {
            try {
                client.sendEvent(Constants.SSE_EMITTER_EVENT_WEBHOOK, data, UUID.randomUUID().toString());
            } catch (Exception e) {
                log.debug("An exception sending to disconnected SSE client, {}", e.getMessage(), e);
                closeClient(boardId, client, e);
            }
        });
    }

    public void sendHeartbeat() {
        if (webhookClients.isEmpty()) {
            log.debug("No SSE clients found, nothing to send");
            return;
        }

        log.trace("Sending SSE heartbeat to {} active boards", webhookClients.size());
        webhookClients.forEach((key, clients) -> clients.forEach(client -> {
            try {
                client.sendEvent(Constants.SSE_EMITTER_EVENT_PING, "heartbeat");
            } catch (Exception e) {
                log.debug("An exception sending to disconnected SSE client, {}", e.getMessage(), e);
                closeClient(key, client, e);
            }
        }));
    }

    public void onShutdown() {
        log.info("Shutting down SSE service: closing {} active boards", webhookClients.size());

        webhookClients.forEach((key, clients) -> clients.forEach(client -> {
            try (client) {
                client.sendEvent(Constants.SSE_EMITTER_EVENT_SHUTDOWN, "shutdown");
            } catch (Exception e) {
                log.debug("Failed to send SSE shutdown event: {}", e.getMessage(), e);
            }
        }));
        webhookClients.clear();
    }

    private void closeClient(String boardId, SseClient client, Exception cause) {
        try {
            client.close();
        } catch (Exception closeException) {
            cause.addSuppressed(closeException);
        } finally {
            cleanup(boardId, client).run();
        }
    }
}
