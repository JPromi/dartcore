package com.jpromi.darts.backend.interceptor;

import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import jakarta.servlet.http.Cookie;

@RequiredArgsConstructor
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final AuthService authService; // dein Service

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletReq)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String sessionCookie = null;
        Cookie[] cookies = servletReq.getServletRequest().getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("dcn.session".equals(c.getName())) {
                    sessionCookie = c.getValue();
                    break;
                }
            }
        }

        if (sessionCookie == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        Session session = authService.session(sessionCookie);
        if (session == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        attributes.put("sessionCookie", sessionCookie);
        return true;
    }

    @Override public void afterHandshake(ServerHttpRequest r, ServerHttpResponse s,
                                         WebSocketHandler h, Exception ex) {}
}
