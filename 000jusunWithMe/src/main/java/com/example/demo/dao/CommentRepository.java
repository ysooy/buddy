package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long> {
	
	 List<Comment> findByPostNo(long postNo);
	
	 Comment findByComNo(long comNo);
}
