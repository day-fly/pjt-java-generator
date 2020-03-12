package com.app.service.generator;

import com.app.common.ConstValue;
import com.app.dto.RequestDto;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.Modifier;
import javax.sql.DataSource;
import javax.validation.Valid;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service {

    final DataSource dataSource;
    final JdbcTemplate jdbcTemplate;

    private String workType;
    private String apiPath;
    private String firstLowerCaseworkType;

    public String make(RequestDto requestDto) {

        workType = requestDto.getFilePrefix();
        String apiGroupPath = requestDto.getApiGroupPath();
        apiPath = requestDto.getApiPath();

        firstLowerCaseworkType = workType.substring(0, 1).toLowerCase() + workType.substring(1);

        //class 생성
        TypeSpec service = TypeSpec.interfaceBuilder(workType + ConstValue.SERVICE)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(getMethod("list", ParameterizedTypeName.get(ClassName.get(List.class), ClassName.bestGuess(workType + ConstValue.SEARCH_VO)),  ConstValue.SEARCH_VO))
                //.addMethod(getMethod("get", ParameterizedTypeName.get(ClassName.bestGuess(workType + ConstValue.VO)), ConstValue.SEARCH_VO))
                .addMethod(getMethod("create", ParameterizedTypeName.get(ClassName.get(int.class)),  ConstValue.VO))
                .addMethod(getMethod("update", ParameterizedTypeName.get(ClassName.get(Integer.class)),  ConstValue.VO))
                .addMethod(getMethod("delete", ParameterizedTypeName.get(ClassName.get(Integer.class)),  ConstValue.VO))
                .build();

        //package 생성
        JavaFile javaFile = JavaFile.builder(requestDto.getPackageName()+ConstValue.SERVICE_PACKAGE, service)
                .build();

        return javaFile.toString();
    }

    private MethodSpec getMethod(String name, ParameterizedTypeName parameterizedTypeName, String voType) {
        return MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .returns(parameterizedTypeName)
                .addParameter(ConstValue.VO.equals(voType) ? getVoParameterSpec() : getSearchVoParameterSpec())
                .build();
    }

    private ParameterSpec getSearchVoParameterSpec() {
        return ParameterSpec.builder(ClassName.bestGuess(workType + ConstValue.SEARCH_VO), firstLowerCaseworkType + ConstValue.SEARCH_VO).build();
    }

    private ParameterSpec getVoParameterSpec() {
        return ParameterSpec.builder(ClassName.bestGuess(workType + ConstValue.VO), firstLowerCaseworkType + ConstValue.VO)
                .build();
    }
}
