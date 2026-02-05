package com.amalitech.smartshop.dtos.responses;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        int currentPage,
        int totalItems,
        int totalPages,
        boolean isLast
) {
}


