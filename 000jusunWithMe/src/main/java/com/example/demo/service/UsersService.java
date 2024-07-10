package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UsersRepository;
import com.example.demo.entity.Users;

@Service
public class UsersService {

    @Autowired
    private UsersRepository dao;
    
    @Value("${kakao.user.password}")
    private String password;
    
    //회원가입
    public Users joinMember(Users u) {
    	u.setPassword(password); //카카오로그인에서는 패스워드 통일.
        return dao.save(u);
    }
    
    //회원찾기: 카카오로그인시 db에서 userNo검색해서 user객체내놓기 
    public Users findMember(long userNo) {
    	return dao.findByUserNo(userNo);
    }
    
}
