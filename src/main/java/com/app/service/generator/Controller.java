package com.app.service;

import com.app.common.ThreadLocal;
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Service
@RequiredArgsConstructor
public class SourceMaker {

    final DataSource dataSource;
    final JdbcTemplate jdbcTemplate;

    private static String POSTFIX_SEARCH_VO = "SearchVO";
    private static String POSTFIX_VO = "VO";
    private static String POSTFIX_CONTROLLER = "Controller";
    private static String POSTFIX_SERVICE = "Service";
    private static String POSTFIX_MAPPER = "Mapper";

    private String workName;
    private String apiPrefixPath;
    private String apiGroupPath;

    public void makeVO(String tableName) throws IOException {
        try(Connection connection = dataSource.getConnection()){
            System.out.println(connection);
            String URL = connection.getMetaData().getURL();
            System.out.println(URL);
            String User = connection.getMetaData().getUserName();
            System.out.println(User);

            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM " + tableName;
            statement.getResultSet();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        jdbcTemplate.execute("INSERT INTO ACCOUNT VALUES(1, 'saelobi')");
    }

    public void makeService() throws IOException {

    }

    public void makeController() throws IOException, ClassNotFoundException {

        workName = ThreadLocal.find("workName");
        apiPrefixPath = ThreadLocal.find("apiPrefixPath");
        apiGroupPath= ThreadLocal.find("apiGroupPath");

        String firstLowerCaseWorkName = workName.substring(0, 1).toLowerCase() + workName.substring(1);
        String serviceName = workName + "Service";
        String controllerName = workName + "Controller";
        String searchVOName = workName + "SearchVO";
        String voName = workName + "VO";

        FieldSpec serviceField = FieldSpec.builder(ClassName.bestGuess(workName + "Service"),
                firstLowerCaseWorkName+"Service", Modifier.FINAL)
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
                        ParameterSpec.builder(ClassName.bestGuess(workName + "SearchVO"), firstLowerCaseWorkName+"SearchVO")
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

        javaFile.writeTo(System.out);
    }

    private MethodSpec listMethod(){
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
