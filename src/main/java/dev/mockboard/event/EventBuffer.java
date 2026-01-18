package dev.mockboard.event;

import java.util.List;

public interface EventBuffer<T> {

    void add(DomainEvent<T> event);

    List<DomainEvent<T>> drain(int maxElements);

    int size();
}
