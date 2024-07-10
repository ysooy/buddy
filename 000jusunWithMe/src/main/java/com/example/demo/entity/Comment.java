package com.example.demo.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="comment")
public class Comment {
	
	@Id
	@Column(name="comNo")
	private long comNo;
	
	@Column(nullable=false, name="comContent")
	private String comContent;
	
	@Column(nullable=false, name="comTime")
	private Date comTime;

	@Column(nullable=true, name = "postNo")
	private long postNo;
	
	@Column(nullable=false, name="userNo")
	private long userNo;
	
	
}
