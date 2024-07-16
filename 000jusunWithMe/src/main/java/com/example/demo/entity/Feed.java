package com.example.demo.entity;

import java.time.LocalDate;
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
@Table(name="feed")
public class Feed {
	
	@Id
	@Column(name="feedNo")
	private long feedNo;
	
	//일단 null허용안되게해놓겠음. 나중에 수정
	@Column(nullable=false, name = "feedPhoto")
	private String feedPhoto;

	@Column(nullable=false, name="feedDate")
	private LocalDate feedDate;
	
	@Column(nullable=true, name="feedLocation")
	private String feedLocation;
	
	@Column(nullable=false, name="groupNo")
	private long groupNo;
}
