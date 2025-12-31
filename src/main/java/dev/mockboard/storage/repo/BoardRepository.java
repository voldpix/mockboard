package dev.mockboard.storage.repo;

import dev.mockboard.core.common.doc.BoardDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardRepository extends MongoRepository<BoardDoc, String> {
}
