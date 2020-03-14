package com.app.service.generator;

import com.app.common.ConstValue;
import com.app.dto.RequestDto;
import com.app.util.AppUtil;
import com.squareup.javapoet.*;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.Modifier;
import javax.sql.DataSource;
import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class Controller {

    final DataSource dataSource;
    final JdbcTemplate jdbcTemplate;

    private RequestDto requestDto;

    public String make(RequestDto _requestDto) {

        if(_requestDto == null) return "";

        requestDto = _requestDto;
        FieldSpec serviceField = FieldSpec.builder(ClassName.get(requestDto.getPackageName() + ConstValue.CONTROLLER_PACKAGE, requestDto.getFilePrefix() + ConstValue.SERVICE),
                requestDto.getFirstLowerCaseFilePrefix() + ConstValue.SERVICE, Modifier.FINAL)
                .build();

        //class 생성
        TypeSpec controller = TypeSpec.classBuilder(requestDto.getWorkType() + ConstValue.CONTROLLER)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(RestController.class)
                .addAnnotation(
                        AnnotationSpec.builder(RequestMapping.class)
                                .addMember("value", "$S", requestDto.getApiGroupPath())
                                .addMember("produces", "$S", MediaType.APPLICATION_JSON_VALUE)
                                .build()
                )
                .addAnnotation(Slf4j.class)
                .addAnnotation(RequiredArgsConstructor.class)
                .addField(serviceField)
                .addMethod(getMethod("list", GetMapping.class, ConstValue.METHOD_LIST_POSTFIX, ConstValue.SEARCH_VO, false))
                .addMethod(getMethod("get", GetMapping.class, "", ConstValue.SEARCH_VO, false))
                .addMethod(getMethod("create", PostMapping.class, "", ConstValue.VO, true))
                .addMethod(getMethod("update", PostMapping.class, ConstValue.METHOD_UPDATE_POSTFIX, ConstValue.VO, true))
                .addMethod(getMethod("delete", PostMapping.class, ConstValue.METHOD_DELETE_POSTFIX, ConstValue.VO, false))
                .build();

        //package 생성
        JavaFile javaFile = JavaFile.builder(requestDto.getPackageName() + ".rest", controller)
                .build();

        return javaFile.toString();
    }

    private MethodSpec getMethod(String name, Class mappingClass, String apiPathPostfix, String voType, boolean isValid) {
        return MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(ApiOperation.class)
                        .addMember("value", "$S", requestDto.getWorkName() + " " + AppUtil.getKorean(name))
                        .addMember("notes", "$S", "")
                        .build()
                )
                .addAnnotation(AnnotationSpec.builder(mappingClass)
                        .addMember("value", "$S", requestDto.getApiPath() + apiPathPostfix)
                        .build()
                )
                .returns(ResponseEntity.class)
                .addParameter(ConstValue.VO.equals(voType) ?
                        isValid ? getVoParameterSpec() : getVoParameterSpecNotIncludeValidAnnotation()
                        : getSearchVoParameterSpec())
                .addStatement(
                        "return $T.ok(" + requestDto.getFirstLowerCaseFilePrefix() + "Service." + name + "(" + requestDto.getFirstLowerCaseFilePrefix() + voType + "))", ResponseEntity.class
                )
                .build();
    }

    private ParameterSpec getSearchVoParameterSpec() {
        return ParameterSpec.builder(requestDto.getSearchVoClassName(), requestDto.getFirstLowerCaseFilePrefix() + ConstValue.SEARCH_VO).build();
    }

    private ParameterSpec getVoParameterSpec() {
        return ParameterSpec.builder(requestDto.getVoClassName(), requestDto.getFirstLowerCaseFilePrefix() + ConstValue.VO)
                .addAnnotation(RequestBody.class)
                .addAnnotation(Valid.class)
                .build();
    }

    //@RequestBody @Valid prameter
    private ParameterSpec getVoParameterSpecNotIncludeValidAnnotation() {
        return ParameterSpec.builder(requestDto.getVoClassName(), requestDto.getFirstLowerCaseFilePrefix() + ConstValue.VO)
                .addAnnotation(RequestBody.class)
                .build();
    }
}
