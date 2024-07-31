package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.GroupTable;
import com.example.demo.entity.Users;
import com.example.demo.service.GroupService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FirstpageController {
	
	@Autowired
	private GroupService gs;
	
	//로그인 후 바로 넘어오는 첫화면
	@GetMapping("/firstpage/firstpage")
    public void firstpageView(HttpSession session, Model model) {
		Users loginUser = (Users)session.getAttribute("loginUser");
		long userNo = loginUser.getUserNo();
		List<GroupTable> groupsOfUser = gs.getGroupsByUserNo(userNo);
		model.addAttribute("groups",groupsOfUser);
	}
	
	//nav 1 홈 클릭시
	@GetMapping("/groupHome/firstpage")
	public void groupFirstpageView(HttpSession session, Model model) {
		Users loginUser = (Users)session.getAttribute("loginUser");
		long userNo = loginUser.getUserNo();
		List<GroupTable> groupsOfUser = gs.getGroupsByUserNo(userNo);
		model.addAttribute("groups",groupsOfUser);
	}
	
	@GetMapping("/firstpage/enterGroup")
    public void enterGroupView() {
    }
	
	@GetMapping("/firstpage/makeGroup")
    public void makeGroupView() {
    }
	
}
