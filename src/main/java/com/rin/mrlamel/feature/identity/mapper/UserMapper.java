package com.rin.mrlamel.feature.identity.mapper;

import com.rin.mrlamel.feature.identity.dto.req.RegisterReq;
import com.rin.mrlamel.feature.identity.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterReq registerReq);
}
