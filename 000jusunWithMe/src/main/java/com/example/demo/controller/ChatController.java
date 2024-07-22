package com.example.demo.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ChatMessageRequestDTO;
import com.example.demo.dto.ChatMessageResponseDTO;
import com.example.demo.entity.Chat;
import com.example.demo.service.ChatService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class ChatController {

    @Autowired
    private ChatService cs; 

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO chatMessageRequestDTO) {
        // DTO를 엔티티로 변환
        Chat chatMessage = Chat.builder()
                .messageNo(0) // 메시지 번호는 저장 시 설정.
                .sentTime(new Date())
                .content(chatMessageRequestDTO.getContent())
                .msgFname(chatMessageRequestDTO.getMsgFname())
                .msgType(chatMessageRequestDTO.getMsgType()) // 메시지 타입 설정 (예: 1은 텍스트, 2는 이미지)
                .userNo(chatMessageRequestDTO.getUserNo())
                .groupNo(chatMessageRequestDTO.getGroupNo())
                .build();

        // 메시지 저장
        chatMessage = cs.saveMessage(chatMessage);

        // 엔티티를 DTO로 변환하여 반환
        return new ChatMessageResponseDTO(
                chatMessage.getMessageNo(),
                chatMessage.getGroupNo(),
                chatMessage.getUserNo(),
                chatMessage.getContent(),
                chatMessage.getMsgFname(),
                chatMessage.getMsgType(),
                chatMessage.getSentTime()
        );
    }  
    
    @PostMapping("/uploadImage")
    public ResponseEntity<Map<String, String>> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {	//업로드 파일이 비어있는지 확인
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "Please select a file to upload"));
        }

        try {
            // 이미지 파일 저장 경로 설정
            String uploadDir = new File("src/main/resources/static/images").getAbsolutePath();	// 절대경로 반환
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();	// 디렉토리가 없으면 생성하기(상위 디렉토리까지 생성됨)

            // 고유 파일명 생성 (파일명이 동일한 경우 충돌 방지)
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            // 저장할 파일 경로 설정하기
            File destinationFile = new File(uploadDir + File.separator + uniqueFileName);
            file.transferTo(destinationFile);	// 업로드된 파일을 지정된 대상 파일로 저장(서버 파일 시스템에 저장시 사용)

            // 클라이언트가 접근할 수 있는 경로로 파일명을 반환 
            // (업로드 성공 시 클라이언트가 접근할 수 있는 경로 반환) ==> 업로드된 파일의 url을 JSON형식으로 응답본문에 포함
            return ResponseEntity.ok(Collections.singletonMap("fileName", "/images/" + uniqueFileName));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "이미지 업로드에 실패했습니다."));
            // 파일 저장 중 예외상황 발생
        }
    }
    
 // 이미지 제공
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path file = Paths.get("src/main/resources/static/images").resolve(filename);	// resolve : 기본경로에 추가 경로 결합
            Resource resource = new UrlResource(file.toUri());	//설정된 경로를 UrlResource객체로 변환
            
            // 파일이 존재하고 읽을 수 있다면
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().body(resource);	// 파일 리소스 반환
            } else {
                return ResponseEntity.notFound().build();	// 404 오류 반환
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

//		DTO와 엔티티 분리하며
//    // 웹소켓 메세지 처리
//    @MessageMapping("/chat")
//    @SendTo("/topic/messages")	// /topic 접두사 사용하기 : 스프링 웹소켓,STOMP 메세지 브로커를 사용하는 표준 관례
//    public Chat sendMessage(Chat chatMessage) {
//    	// 현재 시간 설정
//        chatMessage.setSentTime(new Date());
//        // 메세지 저장
//        chatService.saveMessage(chatMessage);
//        return chatMessage;
//    } 
    
    // 채팅 메세지 정보 반환 (특정 그룹)
    @GetMapping("/chat")
    public String getChatGroup(Model model, @RequestParam(defaultValue = "111") int groupNo) {
        List<Chat> messages = cs.getMessagesByGroupNo(groupNo);
        model.addAttribute("messages", messages);
        return "chat/chat";
    }
}
