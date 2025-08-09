package com.rin.mrlamel.common.mapper;

import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.feature.identity.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")

public interface PageableMapper {
    @Mapping(target = "currentSize", source = "page.size")
    @Mapping(target = "currentPage", source = "page.number")
    PageableDto<User> toUserPageableDto(Page<User> page);

}
