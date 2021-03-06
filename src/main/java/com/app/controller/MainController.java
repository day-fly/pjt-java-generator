package com.app.controller;

import com.app.common.ConstValue;
import com.app.dto.ExportResponseDto;
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

import java.io.File;
import java.io.FileWriter;

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
                .voCode(replaceBraket(this.vo.make(requestDto).toString()))
                .controllerCode(replaceBraket(this.controller.make(requestDto).toString()))
                .serviceCode(replaceBraket(this.service.make(requestDto).toString()))
                .serviceImplCode(replaceBraket(this.serviceImpl.make(requestDto).toString()))
                .mapperCode(replaceBraket(this.mapper.make(requestDto).toString()))
                .mapperXmlCode(replaceBraket(this.mapperXML.make(requestDto)))
                .build());
    }

    @PostMapping("/export")
    public ResponseEntity<ExportResponseDto> export(@RequestBody RequestDto requestDto) {
        try{

            requestDto.setClassNames();
            this.vo.make(requestDto).writeTo(new File(requestDto.getSavePath()));
            this.mapper.make(requestDto).writeToFile(new File(requestDto.getSavePath()));
            this.service.make(requestDto).writeToFile(new File(requestDto.getSavePath()));
            this.serviceImpl.make(requestDto).writeToFile(new File(requestDto.getSavePath()));
            this.controller.make(requestDto).writeTo(new File(requestDto.getSavePath()));

            File file = new File(requestDto.getSavePath() + "/" + requestDto.getFilePrefix() + ConstValue.MAPPER + ".xml");
            FileWriter fw = new FileWriter(file);
            fw.write(this.mapperXML.make(requestDto));
            fw.flush();
            fw.close();

            return ResponseEntity.ok(ExportResponseDto.builder().message("파일이 생성되었습니다.").build());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok(ExportResponseDto.builder().message(e.getMessage()).build());
        }
    }

    private String replaceBraket(String fromString) {
        if (StringUtils.isEmpty(fromString)) return "";
        return fromString.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
