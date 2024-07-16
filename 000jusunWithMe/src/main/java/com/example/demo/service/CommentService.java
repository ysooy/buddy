package com.example.demo.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.CommentRepository;
import com.example.demo.entity.Comment;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	//댓글 등록
	public void saveComment(Comment comment) {
		//댓글 등록 시각: 현재시각으로 설정
		comment.setComTime(new Date());
		
		System.out.println(comment);
		commentRepository.save(comment);
	}
}
