package com.app.service.generator;

import com.app.common.ConstValue;
import com.app.dto.RequestDto;
import com.app.util.AppUtil;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service {

    private RequestDto requestDto;

    public JavaFile make(RequestDto _requestDto) {
        requestDto = _requestDto;

        //class 생성
        TypeSpec service = TypeSpec.interfaceBuilder(requestDto.getFilePrefix() + ConstValue.SERVICE)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(getMethod("list", ParameterizedTypeName.get(ClassName.get(List.class), requestDto.getVoClassName()), ConstValue.SEARCH_VO))
                .addMethod(getMethod("get", requestDto.getVoClassName(), ConstValue.SEARCH_VO))
                .addMethod(getMethod("create", int.class, ConstValue.VO))
                .addMethod(getMethod("update", int.class, ConstValue.VO))
                .addMethod(getMethod("delete", int.class, ConstValue.VO))
                .addJavadoc(requestDto.getFilePrefix() + ConstValue.SERVICE + ".java" + "\n\n"
                    + "@author " + requestDto.getAuthor() + "\n"
                    + "@history" + "\n"
                    + " - " + LocalDate.now() + " : 최초 생성"
                )
                .build();

        //package 생성
        JavaFile javaFile = JavaFile.builder(requestDto.getPackageName() + ConstValue.SERVICE_PACKAGE, service)
                .build();

        return javaFile;
    }

    private MethodSpec getMethod(String name, Object returnType, String voType) {
        //파라미터명
        String param = requestDto.getFirstLowerCaseFilePrefix() + voType;
        //파라미터 설명
        String paramDesc = ConstValue.VO.equals(voType) ? requestDto.getWorkName() + " " + AppUtil.getKorean(name) + " Object" : "검색조건 Object";
        return MethodSpec.methodBuilder(name)
                .addJavadoc(requestDto.getWorkName() + " " + AppUtil.getKorean(name) + "\n"
                        + "@param " + param + " : " + paramDesc + "\n"
                        + "@return " + returnType.toString())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(
                        returnType instanceof ParameterizedTypeName ? (ParameterizedTypeName) returnType
                                : returnType instanceof ClassName ? (ClassName) returnType
                                : TypeName.get((Type) returnType)
                )
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
