package dev.mockboard.storage.repo;

import dev.mockboard.core.common.doc.MockRuleDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MockRuleRepository extends MongoRepository<MockRuleDoc, String> {

    List<MockRuleDoc> findByBoardId(String boardId);

    Optional<MockRuleDoc> findByIdAndBoardId(String id, String boardId);

    void deleteByIdAndBoardId(String mockRuleId, String boardId);
}
