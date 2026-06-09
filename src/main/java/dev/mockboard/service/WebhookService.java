package dev.mockboard.service;

import dev.mockboard.Constants;
import dev.mockboard.common.domain.MockExecutionResult;
import dev.mockboard.common.domain.RequestMetadata;
import dev.mockboard.common.domain.dto.BoardDto;
import dev.mockboard.common.domain.dto.WebhookDto;
import dev.mockboard.common.utils.IdGenerator;
import dev.mockboard.repository.model.Webhook;
import dev.mockboard.web.sse.SseManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@RequiredArgsConstructor
public class WebhookService {

    private final SseManager sseManager;
    private final ExecutorService webhookExecutor;
    private final ConcurrentMap<String, Webhook> webhooks = new ConcurrentHashMap<>();

    public List<WebhookDto> getWebhooks(BoardDto boardDto) {
        return findByBoardIdOrderByTimestampDesc(boardDto.getId()).stream()
                .map(this::toDto)
                .toList();
    }

    public void deleteByBoardId(String boardId) {
        webhooks.values().removeIf(webhook -> boardId.equals(webhook.getBoardId()));
    }

    public void processWebhookAsync(String boardId, RequestMetadata metadata, MockExecutionResult result, long executionTime) {
        webhookExecutor.submit(() -> processWebhook(boardId, metadata, result, executionTime));
    }

    private void processWebhook(String boardId, RequestMetadata metadata, MockExecutionResult result, long executionTime) {
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

            var webhook = toModel(webhookDto);
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

    private Webhook toModel(WebhookDto dto) {
        return Webhook.builder()
                .id(dto.getId())
                .boardId(dto.getBoardId())
                .method(dto.getMethod())
                .path(dto.getPath())
                .fullUrl(dto.getFullUrl())
                .queryParams(dto.getQueryParams())
                .headers(dto.getHeaders())
                .body(dto.getBody())
                .contentType(dto.getContentType())
                .statusCode(dto.getStatusCode())
                .matched(dto.getMatched())
                .timestamp(dto.getTimestamp())
                .processingTimeMs(dto.getProcessingTimeMs())
                .build();
    }

    private WebhookDto toDto(Webhook webhook) {
        return WebhookDto.builder()
                .id(webhook.getId())
                .boardId(webhook.getBoardId())
                .method(webhook.getMethod())
                .path(webhook.getPath())
                .fullUrl(webhook.getFullUrl())
                .queryParams(webhook.getQueryParams())
                .headers(webhook.getHeaders())
                .body(webhook.getBody())
                .contentType(webhook.getContentType())
                .statusCode(webhook.getStatusCode())
                .matched(webhook.isMatched())
                .timestamp(webhook.getTimestamp())
                .processingTimeMs(webhook.getProcessingTimeMs())
                .build();
    }
}
