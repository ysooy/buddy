package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.entity.GroupTable;
import com.example.demo.entity.Users;
import com.example.demo.service.GroupService;
import com.example.demo.service.UsersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class InfoController {

	@Autowired
	private GroupService gs;
	@Autowired
	private UsersService us;
	
	@GetMapping("/myInfo/myInfo")
    public void myInfoView() {
		
	};
	
	@GetMapping("/groupInfo/groupInfo/{groupNo}")
    public String groupInfoView(@PathVariable long groupNo, Model model) {
		List<Users> users = new ArrayList<>();
		//groupMember에서 해당 그룹에 들어가있는 userNo를 찾아서 리스트로 올
		List<Long> userNos = gs.fingUserNoByGroupNo(groupNo);
		for(long userNo :userNos) {
			users.add(us.findMember(userNo));
		}
		//그룹 리더 정보 보내기(뷰에서 리더만 보이는 것도 있고 해서 일단 이렇게 해보겠음)
		long leaderUserNo = gs.findLeader(groupNo);
		model.addAttribute("leaderUserNo",leaderUserNo);
		
		model.addAttribute("users",users);
		return "/groupInfo/groupInfo";
	};
	
}
