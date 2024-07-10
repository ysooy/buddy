package com.example.demo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CommentDTO {

	private long comNo;
	private String userName;
	private String profilePhoto;
	private Date comTime;
	private String comContent;
	
    public CommentDTO(long comNo, String userName, String profilePhoto, Date comTime, String comContent) {
        this.comNo = comNo;
        this.userName = userName;
        this.profilePhoto = profilePhoto;
        this.comTime = comTime;
        this.comContent = comContent;
    }
}
