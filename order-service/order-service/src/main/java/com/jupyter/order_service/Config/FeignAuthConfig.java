package com.jupyter.order_service.Config;

import com.jupyter.order_service.Exception.MissingAuthenticationTokenException;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignAuthConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Bean
    public RequestInterceptor authForwardingInterceptor() {
        return requestTemplate -> {
            String token = resolveToken();
            requestTemplate.header(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        };
    }

    private String resolveToken() {
        String securityContextToken = resolveTokenFromSecurityContext();
        if (StringUtils.hasText(securityContextToken)) {
            return securityContextToken;
        }

        String authorizationHeader = resolveAuthorizationHeaderFromRequest();
        if (StringUtils.hasText(authorizationHeader)) {
            return normalizeToToken(authorizationHeader);
        }

        throw new MissingAuthenticationTokenException("Token de autenticação ausente para chamada Feign.");
    }

    private String resolveTokenFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        Object credentials = authentication.getCredentials();
        if (credentials instanceof String credentialValue && StringUtils.hasText(credentialValue)) {
            return normalizeToToken(credentialValue);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof String principalValue && StringUtils.hasText(principalValue)) {
            return normalizeToToken(principalValue);
        }

        return null;
    }

    private String resolveAuthorizationHeaderFromRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest().getHeader(AUTHORIZATION_HEADER);
        }
        return null;
    }

    private String normalizeToToken(String value) {
        if (value.startsWith(BEARER_PREFIX)) {
            return value.substring(BEARER_PREFIX.length()).trim();
        }
        return value.trim();
    }
}
