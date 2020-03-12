package com.app.service.schema;

import com.app.entity.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchemaService {

    final DataSource dataSource;
    final JdbcTemplate jdbcTemplate;

    public List<Column> getColumnInfos(String tableName) {
        try (Connection connection = dataSource.getConnection()) {
            //테이블 스키마 정보
            return jdbcTemplate.query(
                    "select  table_name"
                            + ",  column_name"
                            + ",  data_type"
                            + ",  udt_name"
                            + ",  character_maximum_length"
                            + ",  is_nullable"
                            + " from information_schema.columns"
                            + " where table_name = ?"
                            + " order by ordinal_position"
                    , (rs, rowNum) ->
                            Column.builder()
                                    .tableName(rs.getString("table_name"))
                                    .columnName(rs.getString("column_name"))
                                    .dataType(rs.getString("data_type"))
                                    .udtName(rs.getString("udt_name"))
                                    .characterMaximumLength(rs.getInt("character_maximum_length"))
                                    .isNullable(rs.getString("is_nullable"))
                                    .build()
                    , tableName
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
