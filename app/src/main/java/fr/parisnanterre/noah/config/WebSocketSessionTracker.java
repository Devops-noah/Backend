package fr.parisnanterre.noah.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component // ✅ Keep this as a singleton component
public class WebSocketSessionTracker {
    private final ConcurrentMap<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    public void addSession(Long userId, WebSocketSession session) {
        userSessions.put(userId, session);
        System.out.println("🔗 WebSocket session stored for user ID: " + userId);
    }

    public void removeSession(Long userId) {
        userSessions.remove(userId);
    }

    public WebSocketSession getSession(Long userId) {
        WebSocketSession session = userSessions.get(userId);
        if (session == null) {
            System.err.println("🚨 No WebSocket session found for user ID: " + userId);
        } else {
            System.out.println("✅ WebSocket session retrieved for user ID: " + userId);
        }
        return session;
    }

}