package com.example.demo.entity;

import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "message")
public class Chat {

    @Id
    private ObjectId _id;

    @Field("messageNo")
    private long messageNo;

    @Field("sentTime")
    private Date sentTime;

    @Field("content")
    private String content;

    @Field("msgFname")
    private String msgFname;

    @Field("msgType") 
    private int msgType;

    @Field("userNo")
    private long userNo;

    @Field("groupNo")
    private long groupNo;

    @Field("imgBase64")
    private String imgBase64;
    
    @Field("unread")
    private int unread;
    
    @Builder
    public Chat(long messageNo, Date sentTime, String content, String msgFname, int msgType, long userNo, long groupNo, String imgBase64, int unread) {
        this.messageNo = messageNo;
        this.sentTime = sentTime;
        this.content = content;
        this.msgFname = msgFname;
        this.msgType = msgType;
        this.userNo = userNo;
        this.groupNo = groupNo;
        this.imgBase64 = imgBase64;
        this.unread = unread;
    }
}
