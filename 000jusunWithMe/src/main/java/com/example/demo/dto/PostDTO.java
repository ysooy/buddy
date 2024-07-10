package com.example.demo.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class PostDTO {

	//게시글 관련 데이터 
	private long postNo;
    private String postContent;
    private Date postTime;
    private List<String> postFname;
    private long feedNo;
	
    //사용자 이름, 프로필사진
    private String userName;
    private String profilePhoto;

    public PostDTO(long postNo, String postContent, Date postTime, List<String> postFname, 
            long feedNo, String userName, String profilePhoto) {
		 this.postNo = postNo;
		 this.postContent = postContent;
		 this.postTime = postTime;
		 this.postFname = postFname;
		 this.feedNo = feedNo;
		 this.userName = userName;
		 this.profilePhoto = profilePhoto;
		}
}
