package com.app.entity;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Method {

    private String name;
    private AnnotationSpec annotation;
    private ParameterSpec parameter;
    private CodeBlock contents;
    private Type returnType;


    public static MethodSpec createMethod(Method method){
        return MethodSpec.methodBuilder(method.name)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(method.annotation)
                .returns(method.returnType)
                .addParameter(method.parameter)
                .addStatement(method.contents)
                .build();
    }
}

