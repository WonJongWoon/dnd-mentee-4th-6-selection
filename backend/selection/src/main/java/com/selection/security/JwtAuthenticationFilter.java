package com.selection.security;

import java.io.IOException;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

@AllArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private static final String REQUEST_TOKEN_HEADER_NAME = "X-AUTH-TOKEN";

    private final JwtTokenDecoder jwtTokenDecoder;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = ((HttpServletRequest) request).getHeader(REQUEST_TOKEN_HEADER_NAME);

        if (isValidToken(token)) {
            Authentication authentication = jwtTokenDecoder.getAuthentication(token);
            SecurityContextHolder.getContext()
                .setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private boolean isValidToken(String token) {
        return Objects.nonNull(token)
            && jwtTokenDecoder.isValidToken(token);
    }
}
