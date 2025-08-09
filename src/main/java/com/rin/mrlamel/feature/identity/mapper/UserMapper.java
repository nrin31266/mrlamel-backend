package com.rin.mrlamel.feature.identity.mapper;

import com.rin.mrlamel.feature.identity.dto.req.CreateUserRq;
import com.rin.mrlamel.feature.identity.dto.req.RegisterReq;
import com.rin.mrlamel.feature.identity.dto.req.UpdateProfileRq;
import com.rin.mrlamel.feature.identity.dto.req.UpdateUserReq;
import com.rin.mrlamel.feature.identity.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterReq registerReq);
    void updateUserProfile(UpdateProfileRq updateProfileRq,@MappingTarget User user);
    @Mapping(target = "isActive", source = "active") // Ignore password field during mapping
    User toUser(CreateUserRq createUserRq);
    void updateUser(UpdateUserReq updateUserReq, @MappingTarget User user);

}
