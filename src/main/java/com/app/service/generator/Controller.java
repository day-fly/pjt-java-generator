package com.app.service.generator;

import com.app.common.ConstValue;
import com.app.dto.RequestDto;
import com.squareup.javapoet.*;
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

    private String workType;
    private String apiPath;
    private String firstLowerCaseworkType;

    public String make(RequestDto requestDto) {

        workType = requestDto.getFilePrefix();
        String apiGroupPath = requestDto.getApiGroupPath();
        apiPath = requestDto.getApiPath();

        firstLowerCaseworkType = workType.substring(0, 1).toLowerCase() + workType.substring(1);
        FieldSpec serviceField = FieldSpec.builder(ClassName.bestGuess(workType + ConstValue.SERVICE),
                firstLowerCaseworkType + "Service", Modifier.FINAL)
                .build();

        //class 생성
        TypeSpec controller = TypeSpec.classBuilder(workType + ConstValue.CONTROLLER)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(RestController.class)
                .addAnnotation(
                        AnnotationSpec.builder(RequestMapping.class)
                                .addMember("value", "$S", apiGroupPath)
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
                .addAnnotation(AnnotationSpec.builder(mappingClass)
                        .addMember("value", "$S", apiPath + apiPathPostfix)
                        .build()
                )
                .returns(ResponseEntity.class)
                .addParameter(ConstValue.VO.equals(voType) ?
                        isValid ? getVoParameterSpec() : getVoParameterSpecNotIncludeValidAnnotation()
                        : getSearchVoParameterSpec())
                .addStatement(
                        "return $T.ok(" + firstLowerCaseworkType + "Service." + name + "(" + firstLowerCaseworkType + voType + "))", ResponseEntity.class
                )
                .build();
    }

    private ParameterSpec getSearchVoParameterSpec() {
        return ParameterSpec.builder(ClassName.bestGuess(workType + ConstValue.SEARCH_VO), firstLowerCaseworkType + ConstValue.SEARCH_VO).build();
    }

    private ParameterSpec getVoParameterSpec() {
        return ParameterSpec.builder(ClassName.bestGuess(workType + ConstValue.VO), firstLowerCaseworkType + ConstValue.VO)
                .addAnnotation(RequestBody.class)
                .addAnnotation(Valid.class)
                .build();
    }

    private ParameterSpec getVoParameterSpecNotIncludeValidAnnotation() {
        return ParameterSpec.builder(ClassName.bestGuess(workType + ConstValue.VO), firstLowerCaseworkType + ConstValue.VO)
                .addAnnotation(RequestBody.class)
                .build();
    }
}
