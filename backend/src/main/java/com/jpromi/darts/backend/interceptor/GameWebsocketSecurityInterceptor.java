package com.jpromi.darts.backend.interceptor;

import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.LocationScreen;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.repositories.DartGameRepository;
import com.jpromi.darts.backend.repositories.LocationScreenRepository;
import com.jpromi.darts.backend.services.AuthService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GameWebsocketSecurityInterceptor implements ChannelInterceptor {

    private static final Pattern GAME_TOPIC_PATTERN = Pattern.compile("^/response/game/([0-9a-fA-F-]{36}).*$");
    private static final Pattern SCREEN_GAME_CREATED_TOPIC_PATTERN = Pattern.compile("^/response/location-screen/([^/]+)/game-created$");

    private final LocationScreenRepository locationScreenRepository;
    private final DartGameRepository dartGameRepository;
    private final AuthService authService;

    public GameWebsocketSecurityInterceptor(LocationScreenRepository locationScreenRepository,
                                            DartGameRepository dartGameRepository,
                                            AuthService authService) {
        this.locationScreenRepository = locationScreenRepository;
        this.dartGameRepository = dartGameRepository;
        this.authService = authService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            String screenToken = accessor.getFirstNativeHeader("X-Screen-Token");

            if ((screenToken == null || screenToken.isBlank()) && sessionAttributes != null) {
                screenToken = (String) sessionAttributes.get("screenToken");
            }

            if (screenToken != null && !screenToken.isBlank()) {
                LocationScreen screen = locationScreenRepository
                        .findByTokenWithLocation(screenToken)
                        .orElseThrow(() -> new MessagingException("Screen not authorized"));

                if (Boolean.TRUE.equals(screen.getIsTmp())) {
                    throw new MessagingException("Temporary screens are not authorized");
                }

                sessionAttributes.put("screenToken", screenToken);
                sessionAttributes.put("screenLocationId", screen.getLocation().getId());
                sessionAttributes.remove("sessionCookie");
                return message;
            }

            String sessionCookie = sessionAttributes != null
                    ? (String) sessionAttributes.get("sessionCookie")
                    : null;
            Session session = sessionCookie != null ? authService.session(sessionCookie) : null;

            if (session == null) {
                throw new MessagingException("Session not authorized");
            }

            return message;
        }

        String screenToken = accessor.getSessionAttributes() != null
                ? (String) accessor.getSessionAttributes().get("screenToken")
                : null;

        Long screenLocationId = accessor.getSessionAttributes() != null
                ? (Long) accessor.getSessionAttributes().get("screenLocationId")
                : null;

        if (screenToken == null) {
            return message; // normal user session
        }

        if (StompCommand.SEND.equals(command)) {
            throw new MessagingException("Screens are not allowed to send messages");
        }

        if (StompCommand.SUBSCRIBE.equals(command)) {
            String destination = accessor.getDestination();
            if (destination == null) return message;

            Matcher screenMatcher = SCREEN_GAME_CREATED_TOPIC_PATTERN.matcher(destination);
            if (screenMatcher.matches()) {
                if (!screenMatcher.group(1).equals(screenToken)) {
                    throw new MessagingException("Screen not authorized for this location feed");
                }
                return message;
            }

            Matcher matcher = GAME_TOPIC_PATTERN.matcher(destination);
            if (!matcher.matches()) return message;

            UUID gameUuid = UUID.fromString(matcher.group(1));

            DartGame game = dartGameRepository
                    .findByUuidWithPlayersAndAccounts(gameUuid)
                    .orElseThrow(() -> new MessagingException("Game not found"));

            if (
                    game.getLocation() == null ||
                            screenLocationId == null ||
                            !game.getLocation().getId().equals(screenLocationId)
            ) {
                throw new MessagingException("Screen not authorized for this game");
            }
        }

        return message;
    }
}
