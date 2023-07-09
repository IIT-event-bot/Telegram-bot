package com.project.userService.config;

import com.project.userService.services.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestFilter extends OncePerRequestFilter {
    private final SessionService sessionService;
    private final AuthenticationManager manager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var cookies = request.getCookies();
        var tokenCookie = getCookieByName("session-token", cookies);
        if (tokenCookie == null || !sessionService.isValid(tokenCookie.getValue())) {
            filterChain.doFilter(request, response);
//            throw new IllegalArgumentException("not auth user");
            return;
        }
        String token = tokenCookie.getValue();
        var user = sessionService.getUserByToken(token);
        Authentication authUser = new UsernamePasswordAuthenticationToken(
                user,
                token,
                List.of(new SimpleGrantedAuthority(user.getRole().getName().name()))
        );
//        Authentication auth = manager.authenticate(authUser);
        SecurityContextHolder.getContext().setAuthentication(authUser);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/auth");
    }

    private Cookie getCookieByName(String name, Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }
}
