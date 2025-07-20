package com.rin.mrlamel.feature.identity.service;

import com.rin.mrlamel.feature.identity.dto.req.RegisterReq;
import com.rin.mrlamel.feature.identity.dto.res.AuthRes;
import com.rin.mrlamel.feature.identity.model.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    AuthRes login(String email, String password, HttpServletResponse response);
    AuthRes register(RegisterReq rq, HttpServletResponse response) throws MessagingException;
}
