package com.example.demo.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.CommentRepository;
import com.example.demo.dao.FeedRepository;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Feed;

@Service
public class FeedService {

	@Autowired
	private FeedRepository feedRepository;
	
	//피드 리스팅 
	public List<Feed> listFeed(long groupNo){
		return feedRepository.findByGroupNo(groupNo);
	}
	
	//해당 그룹에서 해당 날짜에 피드 있는지 확인
	public Feed checkFeedExists(long groupNo, LocalDate feedDate) {
		return feedRepository.findByGroupNoAndFeedDate(groupNo, feedDate);
	}
	
	//피드 저장
	public Feed saveFeed(Feed feed) {
		return feedRepository.save(feed);
	}
	
	//피드의 최고 번호 가져오기
	public long getNextFeedNo() {
		return feedRepository.getNextFeedNo();
	}
}
