package com.example.demo.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageResponseDTO {
	
	// 서버 > 클라이언트로 응답(Chat엔티티 조회하여 ResponseDTO로 변환 후 클라이언트에 전달)
    private Long messageNo;
    private int groupNo;
    private int userNo;
    private String content;
    private String msgFname;
    private int msgType;
    private Date sentTime;

    public ChatMessageResponseDTO(Long messageNo, int groupNo, int userNo, String content, String msgFname, int msgType, Date sentTime) {
        this.messageNo = messageNo;
        this.groupNo = groupNo;
        this.userNo = userNo;
        this.content = content;
        this.msgFname = msgFname;
        this.msgType = msgType;
        this.sentTime = sentTime;
    }
}
