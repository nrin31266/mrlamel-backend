package com.rin.mrlamel.feature.identity.service;

import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.feature.identity.dto.req.CreateUserRq;
import com.rin.mrlamel.feature.identity.dto.req.UpdateUserReq;
import com.rin.mrlamel.feature.identity.model.User;
import jakarta.mail.MessagingException;

public interface UserService {

    PageableDto<User> getAllUsers(int page, int size, String sortBy, String sortDirection, String search
    ,String role, String status);

    void createUser(CreateUserRq createUserRq) throws MessagingException;
    void updateUser(String userId, UpdateUserReq updateUserRq);
    User getUserById(String userId);

}
