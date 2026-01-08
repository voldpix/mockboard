package dev.mockboard.event;

import dev.mockboard.event.config.DomainEvent;
import dev.mockboard.event.config.EventQueue;
import dev.mockboard.event.config.EventType;
import dev.mockboard.repository.BoardRepository;
import dev.mockboard.repository.model.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

    private static final int MAX_DRAIN_ELEMS = 200;

    private final EventQueue eventQueue;
    private final BoardRepository boardRepository;

    @Scheduled(fixedDelay = 5_000)
    public void processCreateEvents() {
        processBoards(EventType.CREATE);
    }

    @Scheduled(fixedDelay = 10_000)
    public void processUpdateEvents() {
//        log.debug("processUpdateEvents");
    }

    @Scheduled(fixedDelay = 30_000)
    public void processDeleteEvents() {
        processBoards(EventType.DELETE);
    }

    private void processBoards(EventType type) {
        var events = eventQueue.drain(type, Board.class, MAX_DRAIN_ELEMS);
        if (CollectionUtils.isEmpty(events)) {
//            log.debug("No events found for type {}", type);
            return;
        }

        try {
            var boards = events.stream()
                    .map(DomainEvent::getEntity)
                    .toList();

            switch (type) {
                case CREATE -> {
                    boardRepository.batchInsert(boards);
                    log.debug("Created {} boards in DB", boards.size());
                }
                case UPDATE -> {
                    log.warn("Batch board updates not yet implemented");
                }
                case DELETE -> {
                    var boardIds = events.stream()
                            .map(DomainEvent::getEntity)
                            .map(Board::getId)
                            .toList();
                    boardRepository.batchDelete(boardIds);
                    log.debug("Deleted {} boards from DB", boards.size());
                }
            }
        } catch (Exception e) {
            log.error("Failed to process {} board events", type, e);
        }
    }
}
