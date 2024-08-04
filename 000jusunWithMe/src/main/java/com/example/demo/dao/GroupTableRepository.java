package com.example.demo.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.GroupTable;

import jakarta.transaction.Transactional;

public interface GroupTableRepository extends JpaRepository<GroupTable, Long> {
	
	//그룹 프로필사진 변경
	@Modifying
    @Transactional
    @Query("UPDATE GroupTable g SET g.groupProfilePhoto = :photo WHERE g.groupNo = :groupNo")
    void updateGroupProfilePhoto(long groupNo, String photo);
}
