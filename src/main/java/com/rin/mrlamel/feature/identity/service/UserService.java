package com.rin.mrlamel.feature.identity.service;

import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.identity.dto.req.CreateUserRq;
import com.rin.mrlamel.feature.identity.dto.req.UpdateUserReq;
import com.rin.mrlamel.feature.identity.model.User;
import jakarta.mail.MessagingException;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.List;

public interface UserService {

    PageableDto<User> getAllUsers(int page, int size, String sortBy, String sortDirection, String search
    ,String role, String status);

    void createUser(CreateUserRq createUserRq) throws MessagingException;
    User createUser(User user);

    @PostAuthorize("returnObject != null and (returnObject.role != 'ADMIN' or hasAuthority('MANAGE_ADMIN'))")
    User updateUser(String userId, UpdateUserReq updateUserRq);
    User getUserById(Long userId);
    List<User> getAvailableTeachersForSessions(List<ClassSession> sessions);
    boolean isTeacherAvailableForAllSessions(Long teacherId, List<ClassSession> sessions);
    List<User> getAllTeachers();
    void assignTeacherToSessions(
            Long teacherId,
            List<ClassSession> classSessions
    );

    User getUserByEmail(String email);


}
