package com.example.demo.handler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.example.demo.service.ChatService;
import com.example.demo.vo.Chat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
	
	//  웹소켓 메세지를 처리하는 역할

    private final ChatService chatService; 
    private final ObjectMapper objectMapper;	// JSON파싱을 위한 객체(웹소켓 메세지를 JSON으로)
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    // 여러 사용자가 동시에 접속하여 메세지 주고 받기 때문에, 
    // 각 사용자의 각각의 웹소켓 세션을 관리하며 메세지를 전송하기 위해 사용하는 코드!
    // 여러 스레드가 동시에 세션을 추가, 제거 시 안정성 보장 (각각 독립적으로 작업 수행 가능)

    public WebSocketHandler(ChatService chatService, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.objectMapper = objectMapper;
    }
    
    // 웹소켓 연결 성립 시 세션 연결된 ID 출력하기
    // 새로운 클라이언트가 웹소켓 서버에 연결될 때마다 해당 세션을 sessions에 추가.(연결식별위함)
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("Connected: " + session.getId());
    }
    
    // 메세지 수신 시 호출 (외부에서 직접 호출하지 못하도록 보호)
    // 메세지를 JSON 형식으로 역직렬화하여 Chat 객체로 변환 (content, sentTime 등에 직접 사용하기 위함)
    // ChatService로 db에 저장, 웹소켓 세션에 연결, JSON 형식으로 직렬화 전송.
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    	// 역직렬화(JSON 문자열 > 자바객체)
    	Chat chatMessage = objectMapper.readValue(message.getPayload(), Chat.class);	
        chatMessage.setSentTime(new Date());
        chatService.saveMessage(chatMessage);
        
        // 연결된 클라이언트에게 메세지 전송
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
            	// 직렬화 (자바객체 > JSON문자열로 변환)
                webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
            }
        }
    }
    
    // 웹소켓 연결 닫힐 경우 호출
    // 닫힌 웹소켓 세션 sessions에서 제거 (유효하지 않은 세션 제거)
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Disconnected: " + session.getId());
    }
    
    // 웹소켓 연결 중 에러 발생시 호출(문제 확인위함)
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        sessions.remove(session);
        System.out.println("Error: " + session.getId() + " " + exception.getMessage());
    }
}
