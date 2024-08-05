package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.GroupMember;
import com.example.demo.entity.GroupTable;
import com.example.demo.entity.Users;
import com.example.demo.service.GroupService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FirstpageController {
	
	@Autowired
	private GroupService gs;
	@Autowired
	private HttpSession session;
	
	//로그인 후 바로 넘어오는 첫화면
	@GetMapping("/firstpage/firstpage")
    public void firstpageView(Model model) {
		Users loginUser = (Users)session.getAttribute("loginUser");
		long userNo = loginUser.getUserNo();
		List<GroupTable> groupsOfUser = gs.getGroupsByUserNo(userNo);
		model.addAttribute("groups",groupsOfUser);
	}
	
	//nav 1 홈 클릭시
	@GetMapping("/groupHome/firstpage")
	public void groupFirstpageView( Model model) {
		Users loginUser = (Users)session.getAttribute("loginUser");
		long userNo = loginUser.getUserNo();
		List<GroupTable> groupsOfUser = gs.getGroupsByUserNo(userNo);
		model.addAttribute("groups",groupsOfUser);
	}
	
	//그룹 만들기 뷰
	@GetMapping("/firstpage/makeGroup")
	public void makeGroupView(Model model) {
		String inviteLink = "http://localhost:8080/firstpage/enterGroup";
	}
	
	//그룹 만들기 액션
	@PostMapping("/firstpage/makeGroup")
    public String makeGroupAction(GroupTable g, MultipartFile photoUpload) {
		Users loginUser = (Users)session.getAttribute("loginUser");
		long userNo = loginUser.getUserNo();
		GroupTable insertedGroup = gs.makeGroup(g, photoUpload);
		return "redirect:/firstpage/inviteLink/"+insertedGroup.getGroupNo();
    }
	
	//초대링크 뷰
	@GetMapping("/firstpage/inviteLink/{groupNo}")
	public String inviteLinkView(@PathVariable long groupNo, Model model) {
		model.addAttribute("group",gs.getGroupByGroupNo(groupNo));
		return "/firstpage/inviteLink";
	}
	
	//그룹 참여하기 뷰
	@GetMapping("/firstpage/enterGroup/{groupNo}")
	public String enterGroupView(@PathVariable long groupNo, Model model) {
		GroupTable g = gs.getGroupByGroupNo(groupNo);
		model.addAttribute("group",g);
		return "/firstpage/enterGroup";
	}
	
	//그룹 참여하기 액션
	@PostMapping("/firstpage/enterGroup")
	public String enterGroup(@RequestParam long groupNo) {
		Users loginUser = (Users)session.getAttribute("loginUser");
		long userNo = loginUser.getUserNo();
		GroupMember m = new GroupMember();
		m.setGroupNo(groupNo);
		m.setUserNo(userNo);
		gs.insertGroupMember(m);
		return "redirect:/firstpage/firstpage";
	}
	
	//참여하려는 사람이 이미 그룹 멤버인지 확인(클라이언트에서 ajax)
	@ResponseBody
	@PostMapping("/checkMember")
	public boolean checkMember(long groupNo) {
		boolean re = false;
		Users loginUser = (Users)session.getAttribute("loginUser");
		long userNo = loginUser.getUserNo();
		if(gs.checkMember(userNo, groupNo)!=null) {
			re = true;
		}
		return re;
	}
	
	
}
