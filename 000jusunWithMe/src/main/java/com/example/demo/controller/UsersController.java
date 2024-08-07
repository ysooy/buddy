package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.dao.UsersRepository;
import com.example.demo.entity.Users;
import com.example.demo.service.UsersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsersController {
	
	@Autowired
	private UsersRepository ur;
	
	@Autowired
	private UsersService us;
	
	//회원가입 화면 로드
	@GetMapping("/firstpage/join")
    public void showJoinForm() {
    }
	
	//회원 가입
    @PostMapping("/firstpage/join")
    public String join(Users user, HttpSession session) {
        Users newUser = us.joinMember(user);
        
        //새로 회원가입한 회원(Users 객체)을 세션에 유지 
        session.setAttribute("loginUser", newUser);
        return "redirect:/firstpage/firstpage";
    }
}