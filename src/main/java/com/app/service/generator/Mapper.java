package com.app.service.generator;

import com.app.common.ConstValue;
import com.app.dto.RequestDto;
import com.app.util.AppUtil;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Mapper {

    private RequestDto requestDto;

    public JavaFile make(RequestDto _requestDto) {
        requestDto = _requestDto;

        //class 생성
        TypeSpec mapper = TypeSpec.interfaceBuilder(requestDto.getFilePrefix() + ConstValue.MAPPER)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(org.apache.ibatis.annotations.Mapper.class)
                .addMethod(getMethod("list", ParameterizedTypeName.get(ClassName.get(List.class), requestDto.getVoClassName()), ConstValue.SEARCH_VO))
                .addMethod(getMethod("get", requestDto.getVoClassName(), ConstValue.SEARCH_VO))
                .addMethod(getMethod("create", int.class, ConstValue.VO))
                .addMethod(getMethod("update", int.class, ConstValue.VO))
                .addMethod(getMethod("delete", int.class, ConstValue.VO))
                .addJavadoc(requestDto.getFilePrefix() + ConstValue.MAPPER + ".java" + "\n\n"
                        + "@author " + requestDto.getAuthor() + "\n"
                        + "@history" + "\n"
                        + " - " + LocalDate.now() + " : 최초 생성"
                )
                .build();

        //package 생성

        return JavaFile.builder(requestDto.getPackageName() + ConstValue.MAPPER_PACKAGE, mapper)
                .build();
    }

    private MethodSpec getMethod(String name, Object returnType, String voType) {
        return MethodSpec.methodBuilder(name)
                .addJavadoc(requestDto.getFilePrefix() + ConstValue.MAPPER + "." + name + " : " + requestDto.getWorkName() + " " + AppUtil.getKorean(name))
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
        return ParameterSpec.builder(requestDto.getSearchVoClassName(), requestDto.getFirstLowerCaseFilePrefix() + ConstValue.SEARCH_VO)
                .addAnnotation(AnnotationSpec.builder(Param.class)
                        .addMember("value","$S",requestDto.getFirstLowerCaseFilePrefix() + ConstValue.SEARCH_VO)
                        .build())
                .build();
    }

    private ParameterSpec getVoParameterSpec() {
        return ParameterSpec.builder(requestDto.getVoClassName(), requestDto.getFirstLowerCaseFilePrefix() + ConstValue.VO)
                .addAnnotation(AnnotationSpec.builder(Param.class)
                        .addMember("value","$S",requestDto.getFirstLowerCaseFilePrefix() + ConstValue.VO)
                        .build())
                .build();
    }
}
