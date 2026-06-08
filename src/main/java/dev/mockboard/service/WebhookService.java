package dev.mockboard.service;

import dev.mockboard.Constants;
import dev.mockboard.common.domain.MockExecutionResult;
import dev.mockboard.common.domain.RequestMetadata;
import dev.mockboard.common.domain.dto.BoardDto;
import dev.mockboard.common.domain.dto.WebhookDto;
import dev.mockboard.common.utils.IdGenerator;
import dev.mockboard.config.sse.SseManager;
import dev.mockboard.repository.model.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final ModelMapper modelMapper;
    private final SseManager sseManager;
    private final ConcurrentMap<String, Webhook> webhooks = new ConcurrentHashMap<>();

    public List<WebhookDto> getWebhooks(BoardDto boardDto) {
        return findByBoardIdOrderByTimestampDesc(boardDto.getId()).stream()
                .map(webhook -> modelMapper.map(webhook, WebhookDto.class))
                .toList();
    }

    @Async
    public void processWebhookAsync(String boardId, RequestMetadata metadata, MockExecutionResult result, long executionTime) {
        try {
            log.debug("Processing webhook async [{}] for key: {}", Thread.currentThread(), boardId);
            var webhookDto = new WebhookDto();
            webhookDto.setId(IdGenerator.generateId());
            webhookDto.setBoardId(boardId);
            webhookDto.setMatched(result.matchingMockRuleDto() != null);
            webhookDto.setProcessingTimeMs(executionTime);
            webhookDto.setTimestamp(Instant.now());

            webhookDto.setMethod(metadata.method());
            webhookDto.setPath(metadata.mockPath());
            webhookDto.setFullUrl(metadata.fullUrl());
            webhookDto.setQueryParams(metadata.queryParams());
            webhookDto.setHeaders(metadata.headers());
            webhookDto.setBody(metadata.requestBody());
            webhookDto.setContentType(metadata.contentType());
            webhookDto.setStatusCode(result.statusCode());

            var webhook = modelMapper.map(webhookDto, Webhook.class);
            save(webhook);
            sseManager.broadcast(boardId, webhookDto);
        } catch (Exception e) {
            log.error("Failed to process webhook", e);
        }
    }

    private List<Webhook> findByBoardIdOrderByTimestampDesc(String boardId) {
        return webhooks.values().stream()
                .filter(webhook -> boardId.equals(webhook.getBoardId()))
                .sorted(Comparator.comparing(Webhook::getTimestamp).reversed())
                .limit(Constants.MAX_WEBHOOKS)
                .toList();
    }

    private void save(Webhook webhook) {
        webhooks.put(webhook.getId(), webhook);
        trim();
    }

    private void trim() {
        var grouped = new ConcurrentHashMap<String, List<Webhook>>();
        webhooks.values().forEach(webhook ->
                grouped.computeIfAbsent(webhook.getBoardId(), ignored -> new ArrayList<>()).add(webhook)
        );

        grouped.forEach((boardId, boardWebhooks) -> boardWebhooks.stream()
                .sorted(Comparator.comparing(Webhook::getTimestamp).reversed())
                .skip(Constants.MAX_WEBHOOKS)
                .map(Webhook::getId)
                .forEach(webhooks::remove));
    }
}
