package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.GroupMember;

import jakarta.transaction.Transactional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

	// userNo가 들어가 있는 groupNo의 리스트 반환
	@Query("SELECT gm.groupNo FROM GroupMember gm WHERE gm.userNo = :userNo")
	List<Long> findGroupNoByUserNo(long userNo);

	// groupNo로 그 그룹에 들어가 있는 멤버찾기
	@Query("SELECT gm.userNo FROM GroupMember gm WHERE gm.groupNo = :groupNo")
	List<Long> findUserNoByGroupNo(long groupNo);

	// 그룹 리더 찾기
	@Query("SELECT gm.userNo FROM GroupMember gm WHERE gm.groupNo = :groupNo and gm.leader=1")
	Long findLeader(long groupNo);

	// 원래 리더를 -> 새 리더로 고치기: 2단계라 아래 연속 두 개는 세트로 사용해야 함---
	@Transactional
	@Modifying
	@Query("UPDATE GroupMember gm SET gm.leader = 0 WHERE gm.groupNo = :groupNo and gm.leader=1")
	int resetLeader(long groupNo);
	@Transactional
	@Modifying
	@Query("UPDATE GroupMember gm SET gm.leader = 1 WHERE gm.userNo = :userNo")
	int setNewLeader(long userNo);
	
	//그룹에서 멤버내보내기
	@Transactional
	@Modifying
	@Query("DELETE FROM GroupMember gm WHERE gm.userNo = :userNo AND gm.groupNo = :groupNo")
	int leaveGroup( long userNo, long groupNo);
	
	//해당 그룹에 그 멤버가 이미 참여하고있는지 알기 위함
	GroupMember findByUserNoAndGroupNo(long userNo, long groupNo);
}
