package com.example.demo.service;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.GroupMemberRepository;
import com.example.demo.dao.GroupTableRepository;
import com.example.demo.entity.GroupMember;
import com.example.demo.entity.GroupTable;
import com.example.demo.entity.Users;

import jakarta.servlet.http.HttpSession;

@Service
public class GroupService {
	
	@Autowired
	private GroupTableRepository gtr;
	@Autowired
	private GroupMemberRepository gmr;
	@Autowired
	private HttpSession session; 
	
	//userNo를 통해 이 사람이 들어간 모든 그룹 리스팅
	public List<GroupTable> getGroupsByUserNo(long userNo){
		List<Long> groupNos = gmr.findGroupNoByUserNo(userNo);
		ArrayList<GroupTable> groups = new ArrayList<GroupTable>();
		for(long groupNo: groupNos) {
			groups.add(gtr.findById(groupNo).get());
		}
		return groups;
	}
	
	//groupNo로 해당 group덩어리찾기
	public GroupTable getGroupByGroupNo(long groupNo) {
		return gtr.findById(groupNo).get();
	}
	
	//groupNo로 그 그룹에 들어가 있는 멤버찾기
	public List<Long> fingUserNoByGroupNo(long groupNo){
		return gmr.findUserNoByGroupNo(groupNo);
	}
	
	//그룹 리더 찾기
	public long findLeader(long groupNo) {
		return gmr.findLeader(groupNo);
	}
	
	//그룹 리더 위임하기
	public int resetLeader(long groupNo, long userNo) {
		int re = 0;
		int re1 = gmr.resetLeader(groupNo); //원래 리더의 리더 지위 박탈
		int re2 = gmr.setNewLeader(userNo); //새로운 리더 설정
		if(re1==1 && re2==1) {
			re=1; //db 업데이트가 무사히 완료됐으면 1 반환
		}
		return re;
	}
	
	//그룹에서 멤버 내보내기(쫓아내기 포함)
	public int leaveGroup(long userNo, long groupNo) {
		int re = gmr.leaveGroup(userNo, groupNo);
		return re;
	}
	
	//그룹 생성 1)groupTable에 그룹 추가
	public GroupTable makeGroup(GroupTable g, MultipartFile photoUpload) {
		//==1)groupTable에 그룹 추가==
		//groupDate를 현재 날짜로 지정
		g.setGroupDate(LocalDate.now());
		
		//사진파일 저장하고 db에도 파일명 저장
		String fname = uploadPhoto(photoUpload);
		g.setGroupProfilePhoto(fname);
		
		//groupTable에 INSERT
		GroupTable insertedGroup = gtr.save(g);
		
		//2) 방장을 groupMember에 추가
		long groupNo = insertedGroup.getGroupNo();
		GroupMember newMember = new GroupMember();
		newMember.setGroupNo(groupNo);
		Users loginUser = (Users)session.getAttribute("loginUser");
		newMember.setUserNo(loginUser.getUserNo());
		newMember.setLeader(1); //리더 설정
		insertGroupMember(newMember);
		
		return insertedGroup;
	}
	// 그룹에 새 멤버 추가
	public GroupMember insertGroupMember(GroupMember m) {
		return gmr.save(m);
	}
	
	//====이하 사진 관련 메소드들 ====
	//사진 등록 메소드 -> 파일명 String으로 반환
	public String uploadPhoto(MultipartFile photoUpload) {
		String path = "src/main/resources/static/images";
		String fname = photoUpload.getOriginalFilename();
			 if (fname != null && !fname.isEmpty()) {
	             try {
	                 FileOutputStream fos = new FileOutputStream(path + "/" + fname);
	                 fos.write(photoUpload.getBytes());
	                 fos.close();
	             } catch (Exception e) {
	                 e.printStackTrace();
	             }
	         }
		return fname;
	}
}
