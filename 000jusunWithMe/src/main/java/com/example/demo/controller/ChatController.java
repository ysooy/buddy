package com.example.demo.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import com.example.demo.service.ChatService;
import com.example.demo.vo.Chat;

import java.util.Date;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService; 
    
    // 웹소켓 메세지 처리
    @MessageMapping("/chat")
    @SendTo("/topic/messages")	// /topic 접두사 사용하기 : 스프링 웹소켓,STOMP 메세지 브로커를 사용하는 표준 관례
    public Chat sendMessage(Chat chatMessage) {
    	// 현재 시간 설정
        chatMessage.setSentTime(new Date());
        // 메세지 저장
        chatService.saveMessage(chatMessage);
        return chatMessage;
    }
    
    // 채팅 메세지 정보 반환 (특정 그룹)
    @GetMapping("/chat")
    public String getChatGroup(Model model, @RequestParam(defaultValue = "111") int groupNo) {
        List<Chat> messages = chatService.getMessagesByGroupNo(groupNo);
        model.addAttribute("messages", messages);
        return "chat/chat";
    }
}
