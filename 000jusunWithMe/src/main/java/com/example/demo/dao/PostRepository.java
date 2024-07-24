package com.example.demo.dao;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Chat;
import com.example.demo.entity.Feed;
import com.example.demo.entity.Post;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, ObjectId> {
	// 피드 번호로 게시물 찾기
    List<Post> findByFeedNo(long feedNo); 
    
	//postNo로 Feed찾기 (Comment에서 사용함)
	Feed findByPostNo(long postNo);
}