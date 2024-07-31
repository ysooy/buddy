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
}
