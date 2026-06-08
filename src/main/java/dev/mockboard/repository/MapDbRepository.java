package dev.mockboard.repository;

import org.mapdb.DB;
import org.mapdb.Serializer;

import java.util.concurrent.ConcurrentMap;

abstract class MapDbRepository<T> {

    protected final DB db;
    protected final ConcurrentMap<String, T> map;

    protected MapDbRepository(DB db, String mapName) {
        this.db = db;
        this.map = openMap(db, mapName);
    }

    @SuppressWarnings("unchecked")
    private ConcurrentMap<String, T> openMap(DB db, String mapName) {
        return db
                .hashMap(mapName, Serializer.STRING, Serializer.JAVA)
                .createOrOpen();
    }

    protected void commit() {
        db.commit();
    }
}
