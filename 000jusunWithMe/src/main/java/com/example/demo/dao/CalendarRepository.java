package com.example.demo.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Calendar;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
	
	// 특정 그룹 캘린더 db 조회
    List<Calendar> findByGroupNo(Long groupNo);

}
