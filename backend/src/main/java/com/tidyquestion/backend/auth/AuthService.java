package com.tidyquestion.backend.auth;

import com.tidyquestion.backend.auth.dto.LoginRequest;
import com.tidyquestion.backend.auth.dto.LoginResponse;
import com.tidyquestion.backend.common.BusinessException;
import com.tidyquestion.backend.common.ErrorCode;
import com.tidyquestion.backend.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 登录服务。
 *
 * 当前是单用户优先模式：使用配置文件中的账号密码完成登录。
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final long DEFAULT_USER_ID = 1L;

    private final String configuredUsername;
    private final String configuredPassword;
    private final int expireHours;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(@Value("${app.auth.username}") String configuredUsername,
                       @Value("${app.auth.password}") String configuredPassword,
                       @Value("${app.jwt.expire-hours}") int expireHours,
                       JwtTokenProvider jwtTokenProvider) {
        this.configuredUsername = configuredUsername;
        this.configuredPassword = configuredPassword;
        this.expireHours = expireHours;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 校验凭证并签发 token。
     */
    public LoginResponse login(LoginRequest request) {
        if (!configuredUsername.equals(request.username()) || !configuredPassword.equals(request.password())) {
            log.warn("module=auth action=login result=failed username={}", request.username());
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }

        String token = jwtTokenProvider.generateToken(DEFAULT_USER_ID, request.username());
        log.info("module=auth action=login result=success username={} userId={}", request.username(), DEFAULT_USER_ID);
        return new LoginResponse(DEFAULT_USER_ID, request.username(), token, expireHours);
    }
}
