package com.app.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Builder
@Data
public class Column {
    String tableName;
    String columnName;
    String dataType;
    String udtName;
    Integer characterMaximumLength;
    String isNullable;
}
