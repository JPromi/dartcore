package com.jpromi.darts.backend.configuration;

import com.jpromi.darts.backend.interceptor.AuthHandshakeInterceptor;
import com.jpromi.darts.backend.services.AuthService;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final AuthService authService;

    public WebsocketConfiguration(AuthService authService) {
        this.authService = authService;
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new AuthHandshakeInterceptor(authService))
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/response");
        config.setApplicationDestinationPrefixes("/data");
    }

}
