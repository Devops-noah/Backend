package fr.parisnanterre.noah.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

// Enables raw WebSocket (not STOMP)
public class RawWebSocketHandler extends TextWebSocketHandler {
    private final WebSocketSessionTracker sessionTracker;

    // ✅ Correct constructor with dependency injection
    public RawWebSocketHandler(WebSocketSessionTracker sessionTracker) {
        this.sessionTracker = sessionTracker;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query == null || !query.contains("userId=")) {
            throw new IllegalArgumentException("Missing userId in WebSocket URL");
        }
        Long userId = Long.parseLong(query.split("userId=")[1]);
        sessionTracker.addSession(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = session.getUri().getQuery().split("=")[1];
        sessionTracker.removeSession(Long.parseLong(userId));
    }
}

