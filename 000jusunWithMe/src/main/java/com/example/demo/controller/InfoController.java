package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.entity.GroupTable;
import com.example.demo.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
public class InfoController {

	@GetMapping("/myInfo/myInfo")
    public void myInfoView() {
		
	};
	
	@GetMapping("/groupInfo/groupInfo/{groupNo}")
    public String groupInfoView(@PathVariable long groupNo,HttpSession session) {
//		GroupTable selectedGroup = (GroupTable)session.getAttribute("selectedGroup");
//		List<Users> users = new ArrayList<>();
//		//groupMember에서 해당 그룹에 들어가있는 userNo를 찾아서 리스트로 올
		return "/groupInfo/groupInfo";
	};
	
}
