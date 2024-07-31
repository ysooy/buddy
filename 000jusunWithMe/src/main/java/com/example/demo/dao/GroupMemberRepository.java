package com.example.demo.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
	


}
