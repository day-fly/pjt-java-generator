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
    private String tableName;
    private String filePrefix;
    private String packageName;
    private String apiGroupPath;
    private String apiPath;

    private ClassName voClassName;
    private ClassName searchVoClassName;

    private String workName; //업무명(한글)

    public String getFirstLowerCaseFilePrefix() {
        if (StringUtils.isEmpty(this.filePrefix)) {
            return "";
        }
        return this.filePrefix.substring(0, 1).toLowerCase() + this.filePrefix.substring(1);
    }

    public void setClassNames() {
        voClassName = ClassName.get(this.packageName + ConstValue.VO_PACKAGE, this.filePrefix + ConstValue.VO);
        searchVoClassName = ClassName.get(this.packageName + ConstValue.VO_PACKAGE, this.filePrefix + ConstValue.SEARCH_VO);
    }
}
