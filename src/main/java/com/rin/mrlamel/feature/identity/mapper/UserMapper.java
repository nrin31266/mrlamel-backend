package com.rin.mrlamel.feature.identity.mapper;

import com.rin.mrlamel.feature.identity.dto.req.RegisterReq;
import com.rin.mrlamel.feature.identity.dto.req.UpdateProfileRq;
import com.rin.mrlamel.feature.identity.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterReq registerReq);
    void updateUserProfile(UpdateProfileRq updateProfileRq,@MappingTarget User user);
}
