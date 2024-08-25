package com.example.demo.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.example.demo.dao.ChatRepository;
import com.example.demo.entity.Chat;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    // 각 채팅방 별 메세지 조회
    public List<Chat> getMessagesByGroupNo(long groupNo) {
        return chatRepository.findByGroupNo(groupNo);
    }

    // 메세지 저장 (몽고db messageNo가 max의 +1 값이 되도록)
    public Chat saveMessage(Chat chat) {
        // 쿼리객체 생성
        Query query = new Query();
        // 쿼리 조건 추가 (groupNo가 chat.getGroupNo와 일치하는 것 찾기
        query.addCriteria(Criteria.where("groupNo").is(chat.getGroupNo()));
        // 쿼리 정렬 조건 추가 : messageNo 필드를 기준으로 내림차순 정렬
        query.with(Sort.by(Sort.Direction.DESC, "messageNo"));
        // 가장 마지막 도큐먼트 1개 찾기
        query.limit(1);

        // groupNo에 해당하는 가장 큰 messageNo 찾기
        Chat lastChat = mongoTemplate.findOne(query, Chat.class);
        // 메세지 저장 시 messageNo 설정
        // (null이면 1, null이 아니면 최대 messageNo+1)
        long newMessageNo = (lastChat != null) ? lastChat.getMessageNo() + 1 : 1;
        // 새로 등록한 메세지의 messageNo를 chat에 설정하기
        chat.setMessageNo(newMessageNo);
        // 현재 시간 sentTime 설정
        chat.setSentTime(new Date());

        // chat객체를 db에 저장하고 객체 반환
        return chatRepository.save(chat);
    }
    
    // messageNo를 기준으로 채팅 메시지 조회
    public Chat getMessageByMessageNo(long messageNo) {
        return chatRepository.findByMessageNo(messageNo);
    }
}
