package com.rin.mrlamel.feature.identity.controller;

import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService; // Uncomment and inject the service when implemented


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiRes<PageableDto<User>> getUsers(
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "createdAt", required = false) String sort,
            @RequestParam(name = "direction", defaultValue = "DESC", required = false) String direction,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "status", required = false) String status
    ) {
        log.info("Fetching users with page: {}, size: {}, sort: {}, direction: {}, search: {}, role: {}, status: {}",
                page, size, sort, direction, search, role, status);
        return ApiRes.success(
                userService.getAllUsers(
                        page - 1, // Adjusting page number to be zero-based
                        size,
                        sort,
                        direction,
                        search,
                        role,
                        status
                )
        );
    }
}
