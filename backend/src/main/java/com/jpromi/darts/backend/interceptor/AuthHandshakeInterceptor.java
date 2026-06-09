package com.jpromi.darts.backend.interceptor;

import com.jpromi.darts.backend.entities.LocationScreen;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.repositories.LocationScreenRepository;
import com.jpromi.darts.backend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import jakarta.servlet.http.Cookie;

public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final AuthService authService;
    private final LocationScreenRepository locationScreenRepository;

    public AuthHandshakeInterceptor(AuthService authService, LocationScreenRepository locationScreenRepository) {
        this.authService = authService;
        this.locationScreenRepository = locationScreenRepository;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletReq)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String sessionCookie = null;
        String screenToken = null;
        Cookie[] cookies = servletReq.getServletRequest().getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("dcn.session".equals(c.getName())) sessionCookie = c.getValue();
                if ("dcn.screen".equals(c.getName())) screenToken = c.getValue();
            }
        }

        if (sessionCookie != null) {
            Session session = authService.session(sessionCookie);
            if (session == null) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
            attributes.put("sessionCookie", sessionCookie);
            return true;
        }

        if (screenToken != null) {
            LocationScreen screen = locationScreenRepository.findByTokenWithLocation(screenToken).orElse(null);
            if (screen == null || Boolean.TRUE.equals(screen.getIsTmp())) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
            attributes.put("screenToken", screenToken);
            return true;
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest r, ServerHttpResponse s,
                               WebSocketHandler h, Exception ex) {}
}
