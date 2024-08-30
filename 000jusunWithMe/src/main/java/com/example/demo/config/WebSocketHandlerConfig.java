package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

//import com.example.demo.handler.WebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketHandlerConfig implements WebSocketConfigurer {
	
	// 웹소켓 핸들러 등록하고 특정 경로에서 웹소켓 연결 허용

//    @Autowired 
//    private WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(webSocketHandler, "/ws")
//                .setAllowedOriginPatterns("*")
//                .withSockJS();
    }
}
