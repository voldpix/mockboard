package dev.mockboard.event;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DedupBuffer<T> implements EventBuffer<T> {

    private final Map<String, DomainEvent<T>> map = new ConcurrentHashMap<>();

    @Override
    public void add(DomainEvent<T> event) {
        if (event.getEntityId() == null) return;

        map.compute(event.getEntityId(), (id, existing) -> {
           if (existing == null) return event;

           if (existing.getType() == EventType.CREATE && event.getType() == EventType.UPDATE) {
               return new DomainEvent<>(
                       EventType.CREATE,
                       event.getEntityId(),
                       event.getEntity(),
                       event.getEntityClass()
               );
           }
           return event;
        });
    }

    @Override
    public List<DomainEvent<T>> drain(int maxElements) {
        if (map.isEmpty()) return Collections.emptyList();

        var batch = new ArrayList<DomainEvent<T>>();
        var it = map.values().iterator();
        while (it.hasNext() && batch.size() < maxElements) {
            batch.add(it.next());
            it.remove();
        }
        return batch;
    }

    @Override
    public int size() {
        return map.size();
    }
}
