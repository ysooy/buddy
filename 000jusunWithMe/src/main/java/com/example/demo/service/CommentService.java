package com.example.demo.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.CommentRepository;
import com.example.demo.dao.PostRepository;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Feed;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private PostRepository postRepository;
	
	//댓글 등록
	public void saveComment(Comment comment) {
		//댓글 등록 시각: 현재시각으로 설정
		comment.setComTime(new Date());
		commentRepository.save(comment);
	}
	
	//postNo로 피드 찾기
	public Feed findByPostNo(long postNo) {
		return postRepository.findByPostNo(postNo);
	}
	
}
