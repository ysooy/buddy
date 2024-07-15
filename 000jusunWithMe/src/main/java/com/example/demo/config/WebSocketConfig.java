package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
    
    
    //// 웹소켓 메세지의 크기가 STOMP content-length 헤더 제한 초과함 ==> 메세지 크기 제한 늘리기
    
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(200 * 1024); // 메시지 크기 제한을 200KB로 설정
        registration.setSendBufferSizeLimit(512 * 1024); // 보낼 버퍼 크기 제한을 512KB로 설정
        registration.setSendTimeLimit(20 * 10000); // 보내는 시간 제한을 200초로 설정
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        // 연결 이벤트 핸들러
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // 연결 해제 이벤트 핸들러(사용자가 웹 소켓 연결 끊으면 실행)
    }
        
}
