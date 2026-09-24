package com.example.medicalclinicproxy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public record PageDto<T>(
        @Schema(description = "Elements of the current page")
        List<T> content,
        @Schema(description = "Current page number, starting from 0", example = "0")
        int pageNumber,
        @Schema(description = "Requested number of elements per page", example = "20")
        int pageSize,
        @Schema(description = "Total number of elements across all pages", example = "137")
        long totalElements,
        @Schema(description = "Total number of pages", example = "7")
        int totalPages
) {
    public static <T> PageDto<T> from(Page<T> page) {
        return new PageDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
