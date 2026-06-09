package dev.mockboard.repository;

import dev.mockboard.repository.model.MockRule;
import org.mapdb.DB;

import java.util.Comparator;
import java.util.List;

public class MockRuleRepository extends MapDbRepository<MockRule> {

    public MockRuleRepository(DB db) {
        super(db, "mockRules");
    }

    public synchronized MockRule save(MockRule mockRule) {
        map.put(mockRule.getId(), mockRule);
        commit();
        return mockRule;
    }

    public List<MockRule> findByBoardIdOrderByTimestampDesc(String boardId) {
        return map.values().stream()
                .filter(rule -> boardId.equals(rule.getBoardId()))
                .sorted(Comparator.comparing(MockRule::getTimestamp).reversed())
                .toList();
    }

    public synchronized void deleteById(String mockRuleId) {
        map.remove(mockRuleId);
        commit();
    }

    public synchronized void deleteByBoardId(String boardId) {
        var ruleIds = map.values().stream()
                .filter(rule -> boardId.equals(rule.getBoardId()))
                .map(MockRule::getId)
                .toList();
        ruleIds.forEach(map::remove);
        commit();
    }
}
