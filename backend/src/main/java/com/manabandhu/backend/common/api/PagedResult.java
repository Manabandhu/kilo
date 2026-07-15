package com.manabandhu.backend.common.api;

import java.util.List;

public record PagedResult<T>(
        List<T> items,
        int page,
        int size,
        long totalItems,
        int totalPages,
        boolean hasNext) {

    public static <T> PagedResult<T> of(List<T> items, int page, int size, long totalItems) {
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalItems / size);
        boolean hasNext = page + 1 < totalPages;
        return new PagedResult<>(items, page, size, totalItems, totalPages, hasNext);
    }
}
