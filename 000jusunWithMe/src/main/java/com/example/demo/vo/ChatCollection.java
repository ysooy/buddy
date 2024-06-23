package com.example.demo.vo;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Builder;
import lombok.Data;

//채팅: mongoDB연결
@Data
@Document(collection = "buddy")
public class ChatCollection {

    @Id
    private ObjectId _id;

    @Field("messageNo")
    private int messageNo;

    @Field("sentTime")
    private Date sentTime;
    
    @Field("content")
    private String content;
    
    @Field("msgFname")
    private String msgFname;
    
    @Field("msgType")
    private int msgType;
    
    @Field("userNo")
    private int userNo;
    
    @Field("chatroomNo")
    private int chatroomNo;
    

    @Builder
    public ChatCollection(int messageNo, Date sentTime) {
        this.messageNo = messageNo;
        this.sentTime = sentTime;
    }

}