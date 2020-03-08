//package com.app;
//
//import com.app.common.GlobalValue;
//import com.app.service.generator.Controller;
//import com.app.service.generator.Vo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class AppRunner implements ApplicationRunner {
//    final Controller controller;
//    final DataSource dataSource;
//    final JdbcTemplate jdbcTemplate;
//    final Vo vo;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//
//        Map<String,String> globalMap = new HashMap<>();
//
//        globalMap.put("workName", "ProcessVariable");
//        globalMap.put("apiPrefixPath", "/rest/v2/process");
//        globalMap.put("apiGroupPath", "/variable");
//
//        GlobalValue.set(globalMap);
//
////        System.out.println(globalValue.get());
//        //controller.makeController();
//        System.out.println(vo.make("account"));
//
//
//    }
//}
