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
        final String[] where = {""};
        columns.forEach(v -> {
            String camelCaseColumnName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, v.getColumnName().toLowerCase());
            camelCaseColumnName = camelCaseColumnName.substring(0, 1).toLowerCase() + camelCaseColumnName.substring(1);
            if ("Y".equals(v.getIsPk())) {
                where[0] += "      AND " + v.getColumnName().toUpperCase() + " = #{" + requestDto.getFirstLowerCaseFilePrefix() + ConstValue.SEARCH_VO + "." + camelCaseColumnName + "}\n";
            }

            if (columns.indexOf(v) == 0) {
                columnStr[0] += "        " + v.getColumnName().toUpperCase() + "\n";
            } else {
                columnStr[0] += "       ," + v.getColumnName().toUpperCase() + "\n";
            }
        });

        xmlCode += columnStr[0];
        xmlCode += "    FROM " + requestDto.getTableName().toUpperCase() + "\n";
        xmlCode += "</select>\n\n";


        //select query - get
        xmlCode += "<select id=\"get\" resultType=\"" + voPath + "\">\n";
        xmlCode += "    SELECT\n";

        xmlCode += columnStr[0];
        xmlCode += "    FROM " + requestDto.getTableName().toUpperCase() + "\n";
        xmlCode += "    WHERE 1=1\n";
        xmlCode += where[0];
        xmlCode += "</select>\n\n";


        //inesrt query
        xmlCode += "<insert id=\"create\">\n";
        xmlCode += "    INSERT INTO " + requestDto.getTableName().toUpperCase() + "( \n";
        xmlCode += columnStr[0];
        xmlCode += "    )\n";
        xmlCode += "    VALUES (\n";

        final String[] columnStr2 = {""};
        columns.forEach(v -> {
            String camelCaseColumnName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, v.getColumnName().toLowerCase());
            camelCaseColumnName = camelCaseColumnName.substring(0, 1).toLowerCase() + camelCaseColumnName.substring(1);
            if (columns.indexOf(v) == 0) {
                columnStr2[0] += "        #{" + requestDto.getFirstLowerCaseFilePrefix() + ConstValue.VO + "." + camelCaseColumnName + "}\n";
            } else {
                columnStr2[0] += "       ,#{" + requestDto.getFirstLowerCaseFilePrefix() + ConstValue.VO + "." + camelCaseColumnName + "}\n";
            }
        });
        xmlCode += columnStr2[0];
        xmlCode += "    )\n";
        xmlCode += "</insert>\n\n";


        //update query
        xmlCode += "<update id=\"update\">\n";
        xmlCode += "    UPDATE " + requestDto.getTableName().toUpperCase() + "\n";
        xmlCode += "    SET \n";

        final String[] columnStr3 = {""};
        int[] i = {0};
        columns.stream().filter(f -> "N".equals(f.getIsPk())).forEach(v -> {
            String camelCaseColumnName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, v.getColumnName().toLowerCase());
            camelCaseColumnName = camelCaseColumnName.substring(0, 1).toLowerCase() + camelCaseColumnName.substring(1);

            if (i[0]++ == 0) {
                columnStr3[0] += "        " + v.getColumnName().toUpperCase() + " = #{" + requestDto.getFirstLowerCaseFilePrefix() + ConstValue.VO + "." + camelCaseColumnName + "}\n";
            } else {
                columnStr3[0] += "       ," + v.getColumnName().toUpperCase() + " = #{" + requestDto.getFirstLowerCaseFilePrefix() + ConstValue.VO + "." + camelCaseColumnName + "}\n";
            }
        });
        xmlCode += columnStr3[0];
        xmlCode += "    WHERE 1=1\n";
        xmlCode += where[0].replaceAll(ConstValue.SEARCH_VO, ConstValue.VO);
        xmlCode += "</update>\n\n";

        xmlCode += "<delete id=\"delete\">\n";
        xmlCode += "    DELETE FROM " + requestDto.getTableName().toUpperCase() + "\n";
        xmlCode += "    WHERE 1=1\n";
        xmlCode += where[0].replaceAll(ConstValue.SEARCH_VO, ConstValue.VO);
        xmlCode += "</delete>\n\n";
        xmlCode += "</mapper>";
        return xmlCode;
    }
}
