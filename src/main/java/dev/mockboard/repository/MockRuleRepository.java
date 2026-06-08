package dev.mockboard.repository;

import dev.mockboard.repository.model.MockRule;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

@Repository
public class MockRuleRepository {

    private final DB db;
    private final ConcurrentMap<String, MockRule> mockRules;

    public MockRuleRepository(DB db) {
        this.db = db;
        this.mockRules = db
                .hashMap("mockRules", Serializer.STRING, Serializer.JAVA)
                .createOrOpen();
    }

    public synchronized MockRule save(MockRule mockRule) {
        mockRule.markNotNew();
        mockRules.put(mockRule.getId(), mockRule);
        db.commit();
        return mockRule;
    }

    public List<MockRule> findByBoardIdAndDeletedFalseOrderByTimestampDesc(String boardId) {
        return mockRules.values().stream()
                .filter(rule -> boardId.equals(rule.getBoardId()))
                .filter(rule -> !rule.isDeleted())
                .sorted(Comparator.comparing(MockRule::getTimestamp).reversed())
                .toList();
    }

    public synchronized void markDeleted(String mockRuleId) {
        var mockRule = mockRules.get(mockRuleId);
        if (mockRule == null) {
            return;
        }

        mockRule.setDeleted(true);
        mockRules.put(mockRuleId, mockRule);
        db.commit();
    }

    public synchronized int hardDeleteMarkedRules() {
        var deletedIds = new ArrayList<String>();
        mockRules.forEach((id, rule) -> {
            if (rule.isDeleted()) {
                deletedIds.add(id);
            }
        });
        deletedIds.forEach(mockRules::remove);
        if (!deletedIds.isEmpty()) {
            db.commit();
        }
        return deletedIds.size();
    }
}
