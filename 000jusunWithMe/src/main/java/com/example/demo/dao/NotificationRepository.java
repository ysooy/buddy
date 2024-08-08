package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	
    // 특정 groupNo와 userNo 필터링
    List<Notification> findByUserNoAndGroupNo(Long userNo, Long groupNo);

    List<Notification> findByGroupNo(Long groupNo);

    // 현재 로그인한 사용자의 userNo를 제외하고 알림
    List<Notification> findByGroupNoAndUserNoNot(Long groupNo, Long userNo);

	
} 