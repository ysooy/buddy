package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	// STOMP메세지 브로커 활성화하고 STOMP 엔드포인트 등록
	

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
    	// /topic 경로를 메세지 브로커의 간단한 메모리 기반 메세지 브로커로 설정
    	// 클라이언트가 이 경로를 통해 메세지 구독    	
        config.enableSimpleBroker("/topic");
        // 클라이언트가 메세지 보낼 때 사용하는 경로의 접두사 설정
        // /app 경로로 메세지 보내면 서버에서는 메세지 받아서 처리
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹소켓에 연결할 수 있는 엔드포인트 설정!
    	registry.addEndpoint("/websocket")
                .setAllowedOriginPatterns("*")
                .withSockJS();	
    			// SockJS를 사용하여 웹소켓을 지원하지 않는 브라우저에서도 웹소켓 연결 사용 가능
    }
}


//package config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//import handler.WebSocketHandler;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketConfigurer {
//
//    @Autowired
//    private WebSocketHandler webSocketHandler;
//    
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(webSocketHandler, "/chating").withSockJS();
//    }
//
//}
