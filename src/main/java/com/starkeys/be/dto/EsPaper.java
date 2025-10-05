package com.starkeys.be.dto;

import lombok.Getter;

import java.util.Objects;

@Getter
public class EsPaper {
    private String paperId;
    private String doi;
    private String title;
    private String introduction;
    private String method;
    private String result;
    private String discussion;
    private String conclusion;
    private String[] fields;
}
