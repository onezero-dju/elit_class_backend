package org.elitclass.api.config.websocket;

import jakarta.annotation.PostConstruct;
import org.elitclass.api.domain.webide.WebIdeTerminalWebSocket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


    @Bean
    public WebIdeTerminalWebSocket webIdeTerminalWebSocket() {
        return new WebIdeTerminalWebSocket();
    }
}
