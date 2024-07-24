package com.example.demo.dao;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Feed;

public interface FeedRepository extends JpaRepository<Feed, Long> {
	
	//해당 그룹의 feed 리스팅
	List<Feed> findByGroupNoOrderByFeedDateDesc(long groupNo);
	
	//해당 그룹의 같은 날짜에 이미 feed가 있는지 확인하기 위함
	Feed findByGroupNoAndFeedDate(long GroupNo, LocalDate feedDate);
	
	@Query("SELECT IFNULL(MAX(f.feedNo), 0) + 1 FROM Feed f")
	Long getNextFeedNo();



}
