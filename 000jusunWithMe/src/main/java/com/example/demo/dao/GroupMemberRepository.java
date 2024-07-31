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

}
