package com.starkeys.be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class EsPaper {
    private String paperId;
    @JsonProperty("abstract")
    private String paperAbstract;
    private String doi;
    private String title;
    private String introduction;
    private String method;
    private String result;
    private String discussion;
    private String conclusion;
    private String[] fields;
}
