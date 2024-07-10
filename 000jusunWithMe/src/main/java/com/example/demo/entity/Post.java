package com.example.demo.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "post")
public class Post {

    @Id
    private ObjectId _id;

    @Field("postNo")
    private long postNo;

    @Field("postContent")
    private String postContent;

    @Field("postTime")
    private Date postTime;

    @Field("userNo")
    private long userNo;

    @Field("postFname") 
    private List<String> postFname;

    @Field("feedNo")
    private long feedNo;

    @Builder
    public Post(Long postNo, String postContent, Date postTime, long userNo, List<String> postFname, long feedNo) {
        this.postNo = postNo;
        this.postContent = postContent;
        this.postTime = postTime;
        this.userNo = userNo;
        this.postFname = postFname;
        this.feedNo = feedNo;

    }
}
