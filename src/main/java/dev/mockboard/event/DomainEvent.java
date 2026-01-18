package dev.mockboard.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class DomainEvent<T> implements Serializable {

    private EventType type;
    private String entityId;
    private T entity;
    private Class<T> entityClass;

    public static <T> DomainEvent<T> create(T entity, String entityId, Class<T> entityClass) {
        return new DomainEvent<>(EventType.CREATE, entityId, entity, entityClass);
    }

    public static <T> DomainEvent<T> update(T entity, String entityId, Class<T> entityClass) {
        return new DomainEvent<>(EventType.UPDATE, entityId, entity , entityClass);
    }

    public static <T> DomainEvent<T> delete(String entityId, Class<T> entityClass) {
        return new DomainEvent<>(EventType.DELETE, entityId, null, entityClass);
    }
}
