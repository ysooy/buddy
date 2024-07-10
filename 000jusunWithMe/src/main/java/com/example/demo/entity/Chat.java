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
    private int userNo;

    @Field("groupNo")
    private int groupNo;

    @Builder
    public Chat(Long messageNo, Date sentTime, String content, String msgFname, int msgType, int userNo, int groupNo) {
        this.messageNo = messageNo;

    }
}
