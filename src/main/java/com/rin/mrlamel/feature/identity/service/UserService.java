package com.rin.mrlamel.feature.identity.service;

import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.feature.identity.model.User;

public interface UserService {

    PageableDto<User> getAllUsers(int page, int size, String sortBy, String sortDirection, String search
    ,String role, String status);
}
