package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "post")
public class Post {

    @Id
    private ObjectId _id;

    @Field("postNo") //게시물 번호 
    private long postNo;

    @Field("postContent") //게시물 내용(글) 
    private String postContent;

    @Field("postUserDefDate") //게시물 피드 해당 날짜(사용자 지정)
    private LocalDate postUserDefDate;
    
    @Field("postTime") //게시물 작성 시각(자동)
    private LocalDateTime postTime;

    @Field("userNo") //작성자 회원번호 
    private long userNo;

    @Field("postFname") //게시물 사진들
    private List<String> postFname;

    @Field("feedNo") //게시물이 포함된 피드 번호
    private long feedNo;
    
    @Field("groupNo") // 그룹 정보
    private long groupNo; 
     

    @Builder
    public Post(Long postNo, String postContent, LocalDate postUserDefDate, LocalDateTime postTime, long userNo, List<String> postFname, long feedNo, long groupNo) {
        this.postNo = postNo;
        this.postUserDefDate = postUserDefDate;
        this.postContent = postContent;
        this.postTime = postTime;
        this.userNo = userNo;
        this.postFname = postFname;
        this.feedNo = feedNo;
        this.groupNo = groupNo;

    }
}
