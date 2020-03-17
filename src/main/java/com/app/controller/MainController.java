package com.app.controller;

import com.app.dto.RequestDto;
import com.app.dto.ResponseDto;
import com.app.service.generator.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.thymeleaf.util.StringUtils;

@Controller
@RequiredArgsConstructor
public class MainController {

    final com.app.service.generator.Controller controller;
    final Mapper mapper;
    final Vo vo;
    final Service service;
    final ServiceImpl serviceImpl;
    final MapperXML mapperXML;

    @GetMapping("/")
    public String get() {
        return "index";
    }

    @PostMapping("/generate")
    public ResponseEntity<ResponseDto> generate(@RequestBody RequestDto requestDto) {

        requestDto.setClassNames();

        return ResponseEntity.ok(ResponseDto.builder()
                .voCode(replaceBraket(this.vo.make(requestDto)))
                .controllerCode(replaceBraket(this.controller.make(requestDto)))
                .serviceCode(replaceBraket(this.service.make(requestDto)))
                .serviceImplCode(replaceBraket(this.serviceImpl.make(requestDto)))
                .mapperCode(replaceBraket(this.mapper.make(requestDto)))
                .mapperXmlCode(replaceBraket(this.mapperXML.make(requestDto)))
                .build());
    }

    private String replaceBraket(String fromString) {
        if (StringUtils.isEmpty(fromString)) return "";
        return fromString.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
