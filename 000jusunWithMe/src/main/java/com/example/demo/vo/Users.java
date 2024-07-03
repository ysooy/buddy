package com.example.demo.vo;

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
@Table(name="users")
public class Users {
	
	@Id
	@Column(name="userNo")
	private long userNo;
	
	@Column(nullable=false, name="email")
	private String email;
	
	@Column(nullable=false, name="userName")
	private String username;
	
	@Column(nullable=false, name="password")
	private String password;
	
	@Column(nullable=true, name = "profilePhoto")
	private String profilePhoto;
	
	@Column(nullable=false, name="birthday")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
}
