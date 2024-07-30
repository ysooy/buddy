package com.example.demo.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.CommentRepository;
import com.example.demo.dao.PostRepository;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Feed;
import com.example.demo.entity.Post;

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
	
	
	//댓글 삭제
	public void deleteComment(long comNo) {
		Comment comment = commentRepository.findByComNo(comNo);
		commentRepository.delete(comment);
	}
	
	//postNo로 댓글 리스팅
	public List<Comment> findCommentByPostNo(long postNo){
		return commentRepository.findByPostNo(postNo);
	}
	
}
