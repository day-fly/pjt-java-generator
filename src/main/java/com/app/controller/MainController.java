package com.app.controller;

import com.app.dto.RequestDto;
import com.app.dto.ResponseDto;
import com.app.service.generator.Mapper;
import com.app.service.generator.Service;
import com.app.service.generator.ServiceImpl;
import com.app.service.generator.Vo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class MainController {

    final com.app.service.generator.Controller controller;
    final Mapper mapper;
    final Vo vo;
    final Service service;
    final ServiceImpl serviceImpl;

    @GetMapping("/")
    public String get(Model model) {
        return "index";
    }

    @PostMapping("/generate")
    public ResponseEntity<ResponseDto> generate(@RequestBody RequestDto requestDto) throws IOException {

        requestDto.setClassNames();

        return ResponseEntity.ok(ResponseDto.builder()
                .voCode(replaceBraket(this.vo.make(requestDto)))
                .controllerCode(replaceBraket(this.controller.make(requestDto)))
                .serviceCode(replaceBraket(this.service.make(requestDto)))
                .serviceImplCode(null)
                .mapperCode(null)
                .build());
    }

    private String replaceBraket(String fromString) {
        if (StringUtils.isEmpty(fromString)) return "";
        return fromString.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
