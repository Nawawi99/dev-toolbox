package dev.awn.service.sql;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlServiceTest {
    private final SqlService service = new SqlService();

    @Test
    void createsInsertStatementsFromJsonArray() throws Exception {
        String sql = service.insertsFromJson("users", "[{\"id\":1,\"name\":\"Ada\"}]");
        assertTrue(sql.contains("INSERT INTO users"));
        assertTrue(sql.contains("'Ada'"));
    }
}
