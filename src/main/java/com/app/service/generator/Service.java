package com.app.service.generator;

import com.app.common.ConstValue;
import com.app.dto.RequestDto;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.lang.model.element.Modifier;
import javax.sql.DataSource;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service {

    final DataSource dataSource;
    final JdbcTemplate jdbcTemplate;

    private RequestDto requestDto;

    public String make(RequestDto _requestDto) {
        requestDto = _requestDto;



        //class 생성
        TypeSpec service = TypeSpec.interfaceBuilder(requestDto.getFilePrefix() + ConstValue.SERVICE)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(getMethod("list", ParameterizedTypeName.get(ClassName.get(List.class), requestDto.getVoClassName()), ConstValue.SEARCH_VO))
                .addMethod(getMethod("get", requestDto.getVoClassName(), ConstValue.SEARCH_VO))
                .addMethod(getMethod("create", int.class, ConstValue.VO))
                .addMethod(getMethod("update", int.class, ConstValue.VO))
                .addMethod(getMethod("delete", int.class, ConstValue.VO))
                .build();

        //package 생성
        JavaFile javaFile = JavaFile.builder(requestDto.getPackageName() + ConstValue.SERVICE_PACKAGE, service)
                .build();

        return javaFile.toString();
    }

    private MethodSpec getMethod(String name, ParameterizedTypeName parameterizedTypeName, String voType) {
        return MethodSpec.methodBuilder(name)
                .addJavadoc("test")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(parameterizedTypeName)
                .addParameter(ConstValue.VO.equals(voType) ? getVoParameterSpec() : getSearchVoParameterSpec())
                .build();
    }

    private MethodSpec getMethod(String name, Class clazz, String voType) {
        return MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(clazz)
                .addParameter(ConstValue.VO.equals(voType) ? getVoParameterSpec() : getSearchVoParameterSpec())
                .build();
    }

    private MethodSpec getMethod(String name, ClassName className, String voType) {
        return MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(className)
                .addParameter(ConstValue.VO.equals(voType) ? getVoParameterSpec() : getSearchVoParameterSpec())
                .build();
    }

    private ParameterSpec getSearchVoParameterSpec() {
        return ParameterSpec.builder(requestDto.getSearchVoClassName(), requestDto.getFirstLowerCaseFilePrefix() + ConstValue.SEARCH_VO).build();
    }

    private ParameterSpec getVoParameterSpec() {
        return ParameterSpec.builder(requestDto.getVoClassName(), requestDto.getFirstLowerCaseFilePrefix() + ConstValue.VO)
                .build();
    }
}
