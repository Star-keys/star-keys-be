package com.starkeys.be.common.response;

public record PageMeta(
        int page,
        int limit,
        long total
) {
}
