package dev.mockboard.event;

import dev.mockboard.Constants;
import dev.mockboard.repository.BoardRepository;
import dev.mockboard.repository.MockRuleRepository;
import dev.mockboard.repository.WebhookRepository;
import dev.mockboard.repository.model.Board;
import dev.mockboard.repository.model.MockRule;
import dev.mockboard.repository.model.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

    private final EventQueue eventQueue;
    private final BoardRepository boardRepository;
    private final MockRuleRepository mockRuleRepository;
    private final WebhookRepository webhookRepository;

    @Scheduled(
            initialDelay = 5_000,
            fixedDelayString = "#{T(dev.mockboard.Constants).EVENT_FIFO_PROCESS_DELAY}")
    public void processFifoBufferEvents() {
        processBoards();
        processMockRules();
    }

    @Scheduled(
            initialDelay = 10_000,
            fixedDelayString = "#{T(dev.mockboard.Constants).EVENT_DEDUP_PROCESS_DELAY}"
    )
    public void processDedupBufferEvents() {
        processWebhooks();
    }

    private void processBoards() {
        var events = eventQueue.drain(Board.class, Constants.EVENT_CONSUMER_DRAIN_BOARD_ELEMS);
        if (CollectionUtils.isEmpty(events)) return;

        try {
            var groupedEvents = groupByType(events);
            if (groupedEvents.containsKey(EventType.CREATE)) {
                var domainEvents = groupedEvents.get(EventType.CREATE);
                var list = getEntities(domainEvents);
                boardRepository.batchInsert(list);
                log.info("Inserted {} boards", list.size());
            }

            if (groupedEvents.containsKey(EventType.UPDATE)) {
                log.warn("Not implemented yet");
            }

            if (groupedEvents.containsKey(EventType.DELETE)) {
                var domainEvents = groupedEvents.get(EventType.DELETE);
                var ids = getIds(domainEvents);
                boardRepository.batchDelete(ids);
                log.info("Updated {} boards", ids.size());
            }
        } catch (Exception e) {
            log.error("Failed to process board batch", e);
        }
    }

    private void processMockRules() {
        var events = eventQueue.drain(MockRule.class, Constants.EVENT_CONSUMER_DRAIN_MOCK_RULE_ELEMS);
        if (CollectionUtils.isEmpty(events)) return;

        try {
            var groupedEvents = groupByType(events);
            if (groupedEvents.containsKey(EventType.CREATE)) {
                var domainEvents = groupedEvents.get(EventType.CREATE);
                var list = getEntities(domainEvents);
                mockRuleRepository.batchInsert(list);
                log.info("Inserted {} mock rules", list.size());
            }

            if (groupedEvents.containsKey(EventType.UPDATE)) {
                var domainEvents = groupedEvents.get(EventType.UPDATE);
                var list = getEntities(domainEvents);
                mockRuleRepository.batchUpdate(list);
                log.info("Updated {} mock rules", list.size());
            }

            if (groupedEvents.containsKey(EventType.DELETE)) {
                var domainEvents = groupedEvents.get(EventType.DELETE);
                var ids = getIds(domainEvents);
                mockRuleRepository.batchDelete(ids);
                log.info("Updated {} mock rules", ids.size());
            }
        } catch (Exception e) {
            log.error("Failed to process mock rules batch", e);
        }
    }

    private void processWebhooks() {
        var events = eventQueue.drain(Webhook.class, Constants.EVENT_CONSUMER_DRAIN_WEBHOOK_ELEMS);
        if (CollectionUtils.isEmpty(events)) return;

        try {
            var groupedEvents = groupByType(events);
            if (groupedEvents.containsKey(EventType.CREATE)) {
                var domainEvents = groupedEvents.get(EventType.CREATE);
                var list = getEntities(domainEvents);
                webhookRepository.batchInsert(list);
                log.info("Inserted {} webhooks", list.size());
            }

            if (groupedEvents.containsKey(EventType.UPDATE)) {
                var domainEvents = groupedEvents.get(EventType.UPDATE);
                var list = getEntities(domainEvents);
                webhookRepository.batchUpdate(list);
                log.info("Updated {} webhooks", list.size());
            }

            if (groupedEvents.containsKey(EventType.DELETE)) {
                log.warn("Webhook DELETE should not be implemented");
            }
        } catch (Exception e) {
            log.error("Failed to process webhook batch", e);
        }
    }

    private <T>Map<EventType, List<DomainEvent<T>>> groupByType(List<DomainEvent<T>> events) {
        return events.stream()
                .collect(Collectors.groupingBy(DomainEvent::getType));
    }

    private <T>List<T> getEntities(List<DomainEvent<T>> events) {
        return events.stream()
                .map(DomainEvent::getEntity)
                .filter(Objects::nonNull)
                .toList();
    }

    private <T>List<String> getIds(List<DomainEvent<T>> events) {
        return events.stream()
                .map(DomainEvent::getEntityId)
                .filter(Objects::nonNull)
                .toList();
    }
}
