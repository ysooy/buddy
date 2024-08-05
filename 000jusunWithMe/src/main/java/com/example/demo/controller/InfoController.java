package com.example.demo.controller;

import java.util.ArrayList;
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
	@Autowired
	private HttpSession session;
	
	//마이페이지 뷰
	@GetMapping("/myInfo/myInfo")
    public void myInfoView() {
		
	};

	//마이페이지 정보 수정 액션: 프사, 닉네임 수정 가능
	@PostMapping("/myInfo/uploadMyInfo")
	@ResponseBody
	public Users uploadMyInfo(@RequestParam MultipartFile photo, @RequestParam String username) {
		Users user = (Users)session.getAttribute("loginUser");
		if(photo.getOriginalFilename() !=null &&!photo.getOriginalFilename().isEmpty()) {
			String oldFname = user.getProfilePhoto();			
			String newFname = gs.uploadPhoto(photo);
			System.out.println(newFname);
			if(oldFname!=null && !oldFname.isEmpty()) {
				gs.deletePhoto(oldFname);
			}
			user.setProfilePhoto(newFname); //새 프로필사진 설정
		}
		user.setUsername(username); //새 username 설정
		return us.updateProfile(user); //save() 이용
	}
	
	//그룹페이지 뷰 
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
	
	//리더를 다른멤버로 위임
	@GetMapping("/groupInfo/changeLeader/{userNo}")
	public String changeLeader(@PathVariable long userNo) {
		GroupTable selectedGroup = (GroupTable)session.getAttribute("selectedGroup");
		long groupNo = selectedGroup.getGroupNo();
		int re = gs.resetLeader(groupNo, userNo); //테스팅 위해 일단 만들어두긴 함
		return "redirect:/groupInfo/groupInfo/"+groupNo;
	}
	
	//멤버를 그룹에서 내보내기 
	@GetMapping("/groupInfo/kickout/{userNo}")
	public String kickout(@PathVariable long userNo) {
		GroupTable selectedGroup = (GroupTable)session.getAttribute("selectedGroup");
		long groupNo = selectedGroup.getGroupNo();
		int re = gs.leaveGroup(userNo, groupNo); //제대로 왔나 확인하고 돌려보내면 좋을듯함. 추후 추가?
		return "redirect:/groupInfo/groupInfo/"+groupNo;
	}
	//그룹 나가기(자발적) *kickout이랑은 랜딩페이지가 달라야 해서 메소드 그냥 따로 만들었음
	@GetMapping("/groupInfo/leaveGroup/{userNo}")
	public String leaveGroup(@PathVariable long userNo) {
		GroupTable selectedGroup = (GroupTable)session.getAttribute("selectedGroup");
		long groupNo = selectedGroup.getGroupNo();
		int re = gs.leaveGroup(userNo, groupNo);
		return "redirect:/groupHome/firstpage";
	}
	
	//그룹 프사 바꾸기
	@PostMapping("/groupInfo/uploadProfilePhoto")
	@ResponseBody
	public void changeGroupPhoto(@RequestParam MultipartFile photo) {
		GroupTable selectedGroup = (GroupTable)session.getAttribute("selectedGroup");
		long groupNo = selectedGroup.getGroupNo();
		String oldFname = selectedGroup.getGroupProfilePhoto();
		String newFname = gs.uploadPhoto(photo);
		if(oldFname!=null && !oldFname.isEmpty()) {
			gs.deletePhoto(oldFname);
		}
		gs.updateProfilePhoto(groupNo, newFname); //프로필사진 db에 업데이트
	}
	
	
}
