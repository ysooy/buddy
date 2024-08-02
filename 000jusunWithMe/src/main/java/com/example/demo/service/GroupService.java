package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.GroupMemberRepository;
import com.example.demo.dao.GroupTableRepository;
import com.example.demo.entity.GroupTable;

import jakarta.servlet.http.HttpSession;

@Service
public class GroupService {
	
	@Autowired
	private GroupTableRepository gtr;
	@Autowired
	private GroupMemberRepository gmr;
	
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
}
