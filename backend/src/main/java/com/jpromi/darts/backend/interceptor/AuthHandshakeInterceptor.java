package com.jpromi.darts.backend.interceptor;

import com.jpromi.darts.backend.entities.LocationScreen;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.repositories.LocationScreenRepository;
import com.jpromi.darts.backend.services.AuthService;
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

        String screenTokenParameter = servletReq.getServletRequest().getParameter("screenToken");
        if (screenTokenParameter != null && !screenTokenParameter.isBlank()) {
            screenToken = screenTokenParameter;
        }

        if (screenToken != null) {
            attributes.put("screenToken", screenToken);
        }

        if (sessionCookie != null) {
            attributes.put("sessionCookie", sessionCookie);
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest r, ServerHttpResponse s,
                               WebSocketHandler h, Exception ex) {}
}
