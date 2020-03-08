package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
