package com.rin.mrlamel.common.mapper;

import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import com.rin.mrlamel.feature.identity.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageableMapper {

    public <T> PageableDto<T> toPageableDto(Page<T> page) {
        return PageableDto.<T>builder()
                .currentSize(page.getSize())
                .currentPage(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .empty(page.isEmpty())
                .first(page.isFirst())
                .last(page.isLast())
                .content(page.getContent())
                .build();
    }
}

