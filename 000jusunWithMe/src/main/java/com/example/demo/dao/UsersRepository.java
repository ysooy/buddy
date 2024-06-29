package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.vo.Users;

public interface UsersRepository extends JpaRepository<Users, Integer> {
	
	Users findByUserNo(long userNo);
	
}
