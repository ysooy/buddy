package com.example.demo.service;

import com.example.demo.dao.CalendarRepository;
import com.example.demo.entity.Calendar;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository cr;

    // 일정 등록
    public Calendar saveDiary(Calendar allEvent) {
        return cr.save(allEvent);
    }
    
    // 일정 불러오기
    public List<Calendar> getAllDiary() {
        return cr.findAll();
    }
    
    // 일정 조회
    public Calendar getDiaryById(long calendarNo) {
        return cr.findById(calendarNo).orElse(null);
    }    
    
    // 일정 삭제
    public void deleteDiary(long calendarNo) {
    	cr.deleteById(calendarNo);
    }
    
    // 특정 그룹 일정만 가져오기
    public List<Calendar> getDiaryByGroupNo(Long groupNo) {
        List<Calendar> result = cr.findByGroupNo(groupNo);
        return result;
    }

}
