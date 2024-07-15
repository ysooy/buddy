package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequestDTO {
	
	// 클라이언트 > 서버로 메세지 보내기 (서버에서 DTO를 받아 Chat 엔티티로 변환 후 db저장)
    private int groupNo;
    private int userNo;
    private String content;
    private String msgFname;
    private int msgType; // 메시지 타입 추가 (1은 텍스트, 2는 이미지)    
    private String imgBase64;  // 이미지 파일의 Base64 인코딩 데이터
    
}
