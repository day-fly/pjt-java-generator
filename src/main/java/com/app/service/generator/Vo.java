package com.app.service.generator;

import com.app.dto.RequestDto;
import com.app.entity.Column;
import com.squareup.javapoet.*;
import lombok.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import javax.sql.DataSource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Vo {
    final DataSource dataSource;
    final JdbcTemplate jdbcTemplate;

    public String make(RequestDto requestDto) {
        try (Connection connection = dataSource.getConnection()) {
            String URL = connection.getMetaData().getURL();
            String User = connection.getMetaData().getUserName();

            //테이블 스키마 정보
            List<Column> columns = jdbcTemplate.query(
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
                    , requestDto.getTableName()
            );

            if (columns == null) return "";

            List<FieldSpec> fieldSpecList = new ArrayList<>();
            columns.forEach(v -> {
                String className = "";
                List<AnnotationSpec> annotationSpecs = new ArrayList<>();

                switch (v.getUdtName()) {
                    case ("int4"):
                        className = "Integer";
                        break;
                    default:
                        className = "String";
                        annotationSpecs.add(
                                AnnotationSpec.builder(Size.class)
                                        .addMember("max", "$S", v.getCharacterMaximumLength())
                                        .build()
                        );
                }

                if ("NO".equals(v.getIsNullable())) {
                    annotationSpecs.add(
                            AnnotationSpec.builder(NotEmpty.class).build()
                    );
                }

                fieldSpecList.add(
                        FieldSpec.builder(ClassName.bestGuess(className),
                                v.getColumnName(), Modifier.PRIVATE)
                                .addAnnotations(annotationSpecs)
                                .build()
                );
            });

            //class 생성
            TypeSpec VoClass = TypeSpec.classBuilder(requestDto.getFilePrefix() + "Vo")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Builder.class)
                    .addAnnotation(Setter.class)
                    .addAnnotation(Getter.class)
                    .addAnnotation(NoArgsConstructor.class)
                    .addAnnotation(AllArgsConstructor.class)
                    .addFields(fieldSpecList)
                    .build();

            JavaFile javaFile = JavaFile.builder(requestDto.getPackageName() + "entity", VoClass)
                    .build();

            return javaFile.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
