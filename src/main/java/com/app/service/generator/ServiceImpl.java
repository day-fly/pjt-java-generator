package com.app.service.generator;

import com.app.common.ConstValue;
import com.app.dto.RequestDto;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

@Service
public class ServiceImpl {

    private RequestDto requestDto;

    public JavaFile make(RequestDto _requestDto) {

        if (_requestDto == null) return null;

        requestDto = _requestDto;
        FieldSpec mapperField = FieldSpec.builder(requestDto.getMapperClassName(),
                requestDto.getFirstLowerCaseFilePrefix() + ConstValue.MAPPER, Modifier.FINAL)
                .build();

        //class 생성
        TypeSpec controller = TypeSpec.classBuilder(requestDto.getFilePrefix() + ConstValue.SERVICE_IMPL)
                .addSuperinterface(requestDto.getServiceClassName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Service.class)
                .addAnnotation(RequiredArgsConstructor.class)
                .addField(mapperField)
                .addMethod(getMethod("list", ConstValue.SEARCH_VO, ParameterizedTypeName.get(ClassName.get(List.class), requestDto.getVoClassName())))
                .addMethod(getMethod("get", ConstValue.SEARCH_VO, requestDto.getVoClassName()))
                .addMethod(getMethod("create", ConstValue.VO, int.class))
                .addMethod(getMethod("update", ConstValue.VO, int.class))
                .addMethod(getMethod("delete", ConstValue.VO, int.class))
                .addJavadoc(requestDto.getFilePrefix() + ConstValue.SERVICE_IMPL + ".java" + "\n\n"
                        + "@author " + requestDto.getAuthor() + "\n"
                        + "@history" + "\n"
                        + " - " + LocalDate.now() + " : 최초 생성"
                )
                .build();

        //package 생성
        return JavaFile.builder(requestDto.getPackageName() + ConstValue.SERVICE_PACKAGE, controller)
                .build();
    }

    private MethodSpec getMethod(String name, String voType, Object returnType) {
        return MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(
                        returnType instanceof ParameterizedTypeName ? (ParameterizedTypeName) returnType
                                : returnType instanceof ClassName ? (ClassName) returnType
                                : TypeName.get((Type) returnType)
                )
                .addParameter(ConstValue.VO.equals(voType) ? getVoParameterSpec() : getSearchVoParameterSpec())
                .addStatement(
                        "return " + requestDto.getFirstLowerCaseFilePrefix() + "Mapper." + name + "(" + requestDto.getFirstLowerCaseFilePrefix() + voType + ")"
                )
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
