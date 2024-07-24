package com.example.demo.service;

import com.example.demo.dao.CalendarRepository;
import com.example.demo.entity.Calendar;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    // 일정 등록
    public Calendar saveDiary(Calendar allEvent) {
        return calendarRepository.save(allEvent);
    }
    
    // 일정 불러오기
    public List<Calendar> getAllDiary() {
        return calendarRepository.findAll();
    }
    
    // 
    public Calendar getDiaryById(Integer calendarNo) {
        return calendarRepository.findById(calendarNo).orElse(null);
    }    
} 
