package com.starkeys.be.common.request;

import org.springframework.data.domain.Pageable;

public record PageRequest(
        Integer page,
        Integer size
) {
    public static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    public static final PageRequest DEFAULT = new PageRequest(DEFAULT_PAGE, DEFAULT_SIZE);

    public PageRequest(Integer page, Integer size) {
        this.page = adjustPage(page);
        this.size = adjustSize(size);
    }

    public int offset() {
        int offset = page * size;
        if (offset + size() > 100) {
            throw new IllegalArgumentException("Page request can't exceed total 100: " + offset + size());
        }
        return offset;
    }

    public Pageable toPageable() {
        return org.springframework.data.domain.PageRequest.of(this.page, this.size);
    }

    private Integer adjustPage(Integer page) {
        if (page == null || page < 0) {
            return DEFAULT_PAGE;
        }

        return page;
    }

    private Integer adjustSize(Integer size) {
        if (size == null || size < 0) {
            return DEFAULT_SIZE;
        }

        return size;
    }
}
