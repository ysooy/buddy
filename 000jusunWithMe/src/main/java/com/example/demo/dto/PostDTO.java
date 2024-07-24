package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class PostDTO {

	//게시글 관련 데이터 
	private long postNo;
    private String postContent;
    private LocalDateTime postTime;
    private LocalDate postUserDefDate;
    private List<String> postFname;
    private long feedNo; //이건 필요없음 확인되면 없애자
	
    //사용자 이름, 프로필사진
    private String userName;
    private String profilePhoto;
    
    //해당 게시글에 달린 댓글들 list
    private List<CommentDTO> comments;

    public PostDTO(long postNo, String postContent, LocalDate postUserDefDate, LocalDateTime postTime, List<String> postFname, long feedNo, String userName, String profilePhoto, List<CommentDTO> comments) {
        this.postNo = postNo;
        this.postContent = postContent;
        this.postUserDefDate = postUserDefDate;
        this.postTime = postTime;
        this.postFname = postFname;
        this.feedNo = feedNo;
        this.userName = userName;
        this.profilePhoto = profilePhoto;
        this.comments = comments;
    }
}
