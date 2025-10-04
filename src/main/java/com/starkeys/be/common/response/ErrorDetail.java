package com.starkeys.be.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetail(
    String code,
    String message,
    Object details
) {
}
