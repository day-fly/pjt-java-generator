package com.app.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResponseDto {

    private String voCode;
    private String serviceCode;
    private String serviceImplCode;
    private String mapperCode;
    private String mapperXmlCode;
    private String controllerCode;
}
