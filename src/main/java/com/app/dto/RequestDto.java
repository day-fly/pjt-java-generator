package com.app.dto;

import com.app.common.ConstValue;
import com.squareup.javapoet.ClassName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.thymeleaf.util.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    private String workType;
    private String workSubType;
    private String tableName;
    private String filePrefix;
    private String packageName;
    private String apiGroupPath;
    private String apiPath;
    private String savePath;

    private ClassName voClassName;
    private ClassName searchVoClassName;
    private ClassName mapperClassName;
    private ClassName serviceClassName;
    private ClassName controllerClassName;

    private String workName; //업무명(한글)
    private String author;

    public String getFirstLowerCaseFilePrefix() {
        if (StringUtils.isEmpty(this.filePrefix)) {
            return "";
        }
        return this.filePrefix.substring(0, 1).toLowerCase() + this.filePrefix.substring(1);
    }

    public void setClassNames() {
        voClassName = ClassName.get(this.packageName + ConstValue.VO_PACKAGE, this.filePrefix + ConstValue.VO);
        searchVoClassName = ClassName.get(this.packageName + ConstValue.VO_PACKAGE, this.filePrefix + ConstValue.SEARCH_VO);
        mapperClassName = ClassName.get(this.packageName + ConstValue.MAPPER_PACKAGE, this.filePrefix + ConstValue.MAPPER);
        serviceClassName = ClassName.get(this.packageName + ConstValue.SERVICE_PACKAGE, this.filePrefix + ConstValue.SERVICE);
        controllerClassName = ClassName.get(this.packageName + ConstValue.CONTROLLER_PACKAGE, this.filePrefix + ConstValue.CONTROLLER);
    }
}
