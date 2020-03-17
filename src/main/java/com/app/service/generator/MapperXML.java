package com.app.service.generator;

import com.app.common.ConstValue;
import com.app.dto.RequestDto;
import com.app.entity.Column;
import com.app.service.schema.SchemaService;
import com.app.util.AppUtil;
import com.squareup.javapoet.*;
import lombok.*;
import org.apache.ibatis.annotations.Param;
import org.apache.tomcat.util.bcel.Const;

import javax.lang.model.element.Modifier;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class MapperXML {

    final SchemaService schemaService;
    private RequestDto requestDto;

    public String make(RequestDto requestDto) {

        String voPath = requestDto.getPackageName() + ConstValue.VO_PACKAGE + "." + requestDto.getFilePrefix() + ConstValue.VO;
        List<Column> columns = schemaService.getColumnInfos(requestDto.getTableName());

        String xmlCode = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n";
        xmlCode += "<mapper namespace=\"" + requestDto.getPackageName() + ConstValue.MAPPER_PACKAGE + "." + requestDto.getFilePrefix() + ConstValue.MAPPER + "\">\n\n";

        xmlCode += "<select id=\"selectUserList\" resultType=\"" + voPath + "\">\n";
        xmlCode += "    SELECT\n";

        final String[] columnStr = {""};
        columns.forEach(v -> {
            if(columns.indexOf(v)  != columns.lastIndexOf(v)) {
                columnStr[0] += "        " + v.getColumnName().toUpperCase() + ",\n";
            }else{
                columnStr[0] += "        " + v.getColumnName().toUpperCase() + "\n";
            }
        });

        xmlCode += columnStr[0] + "\n";
//
//
//
//        List<FieldSpec> fieldSpecList = new ArrayList<>();
//        columns.forEach(v -> {
//            String className = "";
//            List<AnnotationSpec> annotationSpecs = new ArrayList<>();
//
//            switch (v.getUdtName()) {
//                case ("int4"):
//                    className = "Integer";
//                    break;
//                default:
//                    className = "String";
//                    annotationSpecs.add(
//                            AnnotationSpec.builder(Size.class)
//                                    .addMember("max", "$S", v.getCharacterMaximumLength())
//                                    .build()
//                    );
//            }
//            if ("NO".equals(v.getIsNullable())) {
//                annotationSpecs.add(
//                        AnnotationSpec.builder(NotEmpty.class).build()
//                );
//            }
//            fieldSpecList.add(
//                    FieldSpec.builder(ClassName.bestGuess(className),
//                            v.getColumnName(), Modifier.PRIVATE)
//                            .addAnnotations(annotationSpecs)
//                            .build()
//            );
//        });

        xmlCode += "</mapper>";
       return xmlCode;
    }
}
