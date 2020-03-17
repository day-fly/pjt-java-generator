package com.app.service.generator;

import com.app.common.ConstValue;
import com.app.dto.RequestDto;
import com.app.entity.Column;
import com.app.service.schema.SchemaService;
import com.google.common.base.CaseFormat;
import lombok.RequiredArgsConstructor;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class MapperXML {

    final SchemaService schemaService;

    public String make(RequestDto requestDto) {

        String voPath = requestDto.getPackageName() + ConstValue.VO_PACKAGE + "." + requestDto.getFilePrefix() + ConstValue.VO;
        List<Column> columns = schemaService.getColumnInfos(requestDto.getTableName());

        String xmlCode = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n";
        xmlCode += "<mapper namespace=\"" + requestDto.getPackageName() + ConstValue.MAPPER_PACKAGE + "." + requestDto.getFilePrefix() + ConstValue.MAPPER + "\">\n\n";


        //select query - list
        xmlCode += "<select id=\"list\" resultType=\"" + voPath + "\">\n";
        xmlCode += "    SELECT\n";

        final String[] columnStr = {""};
        columns.forEach(v -> {
            if(columns.indexOf(v)  == 0) {
                columnStr[0] += "        " + v.getColumnName().toUpperCase() + "\n";
            }else{
                columnStr[0] += "       ," + v.getColumnName().toUpperCase() + "\n";
            }
        });

        xmlCode += columnStr[0];
        xmlCode += "    FROM " + requestDto.getTableName().toUpperCase() + "\n";
        xmlCode += "<select/>\n\n";


        //select query - get
        xmlCode += "<select id=\"get\" resultType=\"" + voPath + "\">\n";
        xmlCode += "    SELECT\n";

        final String[] columnStr2 = {""};
        final String[] where = {""};
        columns.forEach(v -> {
            String camelCaseColumnName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, v.getColumnName().toLowerCase());
            camelCaseColumnName = camelCaseColumnName.substring(0, 1).toLowerCase() + camelCaseColumnName.substring(1);
            if("Y".equals(v.getIsPk())){
                where[0] += "      AND " + v.getColumnName().toUpperCase() + " = #{" + requestDto.getFirstLowerCaseFilePrefix() + ConstValue.SEARCH_VO + "." + camelCaseColumnName + "}\n";
            }
            if(columns.indexOf(v)  == 0) {
                columnStr2[0] += "        " + v.getColumnName().toUpperCase() + "\n";
            }else{
                columnStr2[0] += "       ," + v.getColumnName().toUpperCase() + "\n";
            }
        });

        xmlCode += columnStr2[0];
        xmlCode += "    FROM " + requestDto.getTableName().toUpperCase() + "\n";
        xmlCode += "    WHERE 1=1\n";
        xmlCode += where[0];
        xmlCode += "<select/>\n\n";



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
