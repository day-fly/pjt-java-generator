package com.app;

import com.app.common.ThreadLocal;
import com.app.service.SourceMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    SourceMaker sourceMaker;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Map<String,String> globalMap = new HashMap<>();

        globalMap.put("workName", "ProcessVariable");
        globalMap.put("apiPrefixPath", "/rest/v2/process");
        globalMap.put("apiGroupPath", "/variable");

        ThreadLocal.set(globalMap);

        System.out.println("test");



//        System.out.println(globalValue.get());
        sourceMaker.makeController();
    }
}
