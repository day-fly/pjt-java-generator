package com.app.service.generator;

import com.app.dto.RequestDto;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.lang.model.element.Modifier;
import javax.sql.DataSource;
import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class Controller {

    final DataSource dataSource;
    final JdbcTemplate jdbcTemplate;

    private String workName;
    private String apiPrefixPath;
    private String apiGroupPath;

    public String make(RequestDto requestDto) {

        workName = requestDto.getFilePrefix();
        apiGroupPath = requestDto.getApiGroupPath();
        apiPrefixPath = requestDto.getApiPath();

        String firstLowerCaseWorkName = workName.substring(0, 1).toLowerCase() + workName.substring(1);
        FieldSpec serviceField = FieldSpec.builder(ClassName.bestGuess(workName + "Service"),
                firstLowerCaseWorkName + "Service", Modifier.FINAL)
                .build();

        //메소드 생성 - get
        MethodSpec getMethod = MethodSpec.methodBuilder("get" + workName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(GetMapping.class)
                        .addMember("value", "$S", apiGroupPath)
                        .build()
                )
                .returns(ResponseEntity.class)
                .addParameter(
                        ParameterSpec.builder(ClassName.bestGuess(workName + "SearchVO"), firstLowerCaseWorkName + "SearchVO")
                                .addAnnotation(RequestBody.class)
                                .addAnnotation(Valid.class)
                                .build()
                )
                //.addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        //메소드 생성 - list


        //class 생성
        TypeSpec helloWorld = TypeSpec.classBuilder(workName + "Controller")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(RestController.class)
                .addAnnotation(
                        AnnotationSpec.builder(RequestMapping.class)
                                .addMember("value", "$S", apiPrefixPath)
                                .addMember("produces", "$S", MediaType.APPLICATION_JSON_VALUE)
                                .build()
                )
                .addAnnotation(Slf4j.class)
                .addAnnotation(RequiredArgsConstructor.class)
                .addField(serviceField)
                .addMethod(getMethod)
                .addMethod(listMethod())
                .build();

        //package 생성
        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();

        return javaFile.toString();
    }

    private MethodSpec listMethod() {
        return MethodSpec.methodBuilder("list" + workName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(GetMapping.class)
                        .addMember("value", "$S", apiGroupPath + "-list")
                        .build()
                )
                .returns(ResponseEntity.class)
                .addParameter(String[].class, "args")
                .addStatement(
                        ""
                )
                //.addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();
    }
}
