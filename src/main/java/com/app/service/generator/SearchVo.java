package com.app.service.generator;

import com.app.common.ConstValue;
import com.app.dto.RequestDto;
import com.app.entity.Column;
import com.app.service.schema.SchemaService;
import com.squareup.javapoet.*;
import lombok.*;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchVo {
    final SchemaService schemaService;

    public JavaFile make(RequestDto requestDto) {
        List<Column> columns = schemaService.getColumnInfos(requestDto.getTableName());
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
                .superclass(ClassName.bestGuess("CommonVO"))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Builder.class)
                .addAnnotation(Setter.class)
                .addAnnotation(Getter.class)
                .addAnnotation(NoArgsConstructor.class)
                .addAnnotation(AllArgsConstructor.class)
                .addFields(fieldSpecList)
                .addJavadoc(requestDto.getFilePrefix() + ConstValue.VO + ".java" + "\n\n"
                        + "@author " + requestDto.getAuthor() + "\n"
                        + "@history" + "\n"
                        + " - " + LocalDate.now() + " : 최초 생성"
                )
                .build();

        JavaFile javaFile = JavaFile.builder(requestDto.getPackageName() + ".entity", VoClass)
                .build();
        //return javaFile.toString();
        return javaFile;
    }
}
