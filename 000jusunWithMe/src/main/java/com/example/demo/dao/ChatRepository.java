package com.example.demo.dao;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Chat;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, ObjectId> {
	// 그룹번호로 채팅방 찾기
    List<Chat> findByGroupNo(long groupNo); 
    
    // MongoDB에 있는 가장 큰 messageNo를 찾고 새롭게 생성
    Chat findTopByOrderByMessageNoDesc();

}