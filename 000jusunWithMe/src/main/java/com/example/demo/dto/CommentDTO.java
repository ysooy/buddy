package com.example.demo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CommentDTO {

	private long comNo;
	private long userNo;
	private String userName;
	private String profilePhoto;
	private Date comTime;
	private String comContent;
	
    public CommentDTO(long comNo, long userNo, String userName, String profilePhoto, Date comTime, String comContent) {
        this.comNo = comNo;
        this.userNo = userNo;
        this.userName = userName;
        this.profilePhoto = profilePhoto;
        this.comTime = comTime;
        this.comContent = comContent;
    }
}
