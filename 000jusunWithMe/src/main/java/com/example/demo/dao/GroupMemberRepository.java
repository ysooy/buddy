package com.example.demo.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
	
	//userNo가 들어가 있는 groupNo의 리스트 반환
	@Query("SELECT gm.groupNo FROM GroupMember gm WHERE gm.userNo = :userNo")
    List<Long> findGroupNoByUserNo(@Param("userNo") long userNo);
	
	//groupNo로 그 그룹에 들어가 있는 멤버찾기
	@Query("SELECT gm.userNo FROM GroupMember gm WHERE gm.groupNo = :groupNo")
	List<Long> findUserNoByGroupNo(@Param("groupNo") long groupNo);
	
	//그룹 리더 찾기 
	@Query("SELECT gm.userNo FROM GroupMember gm WHERE gm.groupNo = :groupNo and gm.leader=1")
	Long findLeader(@Param("groupNo") long groupNo);
}
