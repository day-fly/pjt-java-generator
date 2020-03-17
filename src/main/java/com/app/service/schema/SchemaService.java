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
                    "SELECT  c.TABLE_NAME, c.COLUMN_NAME,c.DATA_TYPE, c.udt_name, c.character_maximum_length, c.is_nullable "
                            + ",CASE WHEN pk.COLUMN_NAME IS NOT NULL THEN 'Y' ELSE 'N' END AS is_pk "
                            + "FROM INFORMATION_SCHEMA.COLUMNS c "
                            + "LEFT JOIN ( "
                            + "SELECT ku.TABLE_CATALOG,ku.TABLE_SCHEMA,ku.TABLE_NAME,ku.COLUMN_NAME "
                            + "FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS AS tc "
                            + "INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS ku "
                            + "ON tc.CONSTRAINT_TYPE = 'PRIMARY KEY' "
                            + "AND tc.CONSTRAINT_NAME = ku.CONSTRAINT_NAME "
                            + ")   pk "
                            + "ON  c.TABLE_CATALOG = pk.TABLE_CATALOG "
                            + "AND c.TABLE_SCHEMA = pk.TABLE_SCHEMA "
                            + "AND c.TABLE_NAME = pk.TABLE_NAME "
                            + "AND c.COLUMN_NAME = pk.COLUMN_NAME "
                            + "WHERE C.TABLE_NAME = ? "
                    , (rs, rowNum) ->
                            Column.builder()
                                    .tableName(rs.getString("table_name"))
                                    .columnName(rs.getString("column_name"))
                                    .dataType(rs.getString("data_type"))
                                    .udtName(rs.getString("udt_name"))
                                    .characterMaximumLength(rs.getInt("character_maximum_length"))
                                    .isNullable(rs.getString("is_nullable"))
                                    .isPk(rs.getString("is_pk"))
                                    .build()
                    , tableName
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
