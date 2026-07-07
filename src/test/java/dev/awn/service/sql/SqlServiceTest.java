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

    @Test
    void visualizesSelectJoinStructure() {
        String graph = service.visualizeStructure("""
                select u.id, count(o.id)
                from users u
                left join orders o on o.user_id = u.id
                where u.active = true
                group by u.id
                order by u.id
                """);

        assertTrue(graph.contains("SQL Visualizer"));
        assertTrue(graph.contains("[FROM]"));
        assertTrue(graph.contains("LEFT JOIN orders o ON o.user_id = u.id"));
        assertTrue(graph.contains("[WHERE]"));
        assertTrue(graph.contains("[GROUP BY]"));
        assertTrue(graph.contains("[SELECT]"));
    }
}
