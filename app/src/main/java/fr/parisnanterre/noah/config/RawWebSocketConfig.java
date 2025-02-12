package fr.parisnanterre.noah.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class RawWebSocketConfig implements WebSocketConfigurer {

    private final WebSocketSessionTracker sessionTracker;

    // ✅ Inject sessionTracker via constructor
    public RawWebSocketConfig(WebSocketSessionTracker sessionTracker) {
        this.sessionTracker = sessionTracker;
    }

    @Bean
    public WebSocketHandler rawWebSocketHandler() {
        // ✅ Pass sessionTracker to the handler's constructor
        return new RawWebSocketHandler(sessionTracker);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(rawWebSocketHandler(), "/ws")
                .setAllowedOrigins("*");
    }
}