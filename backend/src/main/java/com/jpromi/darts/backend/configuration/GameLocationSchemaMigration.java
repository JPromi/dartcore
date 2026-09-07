package com.jpromi.darts.backend.configuration;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class GameLocationSchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public GameLocationSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> obsoleteConstraints = jdbcTemplate.queryForList("""
                select tc.constraint_name
                from information_schema.table_constraints tc
                join information_schema.key_column_usage kcu
                  on tc.constraint_schema = kcu.constraint_schema
                 and tc.constraint_name = kcu.constraint_name
                where tc.table_schema = current_schema()
                  and tc.table_name = 'dart_game'
                  and tc.constraint_type = 'UNIQUE'
                group by tc.constraint_name
                having count(*) = 1
                   and max(kcu.column_name) = 'location_id'
                """, String.class);

        for (String constraint : obsoleteConstraints) {
            String quotedConstraint = constraint.replace("\"", "\"\"");
            jdbcTemplate.execute("alter table dart_game drop constraint \"" + quotedConstraint + "\"");
        }

        jdbcTemplate.execute("""
                create unique index if not exists uk_dart_game_active_location
                on dart_game (location_id)
                where location_id is not null
                  and end_time is null
                  and (is_cancelled = false or is_cancelled is null)
                """);
    }
}
