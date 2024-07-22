package com.example.demo.controller;

import com.example.demo.entity.Calendar;
import com.example.demo.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.time.LocalDate;
import java.util.List;

@Controller
public class CalendarController {

    @Autowired
    private CalendarService cs;

    // 페이지가 로드될 때 캘린더 일정 데이터 모델에 추가한 후 뷰 반환
    @GetMapping("/calendar")
    public String calendar(Model model) {
        List<Calendar> calendarEvents = cs.getAllDiary();
        model.addAttribute("calendarEvents", calendarEvents);
        return "calendar/calendar";
    }

    
    // 등록되어있는 json형식의 일정 데이터를 반환(ajax요청)
    @GetMapping("/calendar/events")
    @ResponseBody
    public List<Calendar> getEvents() {
        return cs.getAllDiary();
    }

    
    // 캘린더 일정 등록
    @PostMapping("/calendar/add")
    @ResponseBody
    public String addEvent(@RequestParam String eventContent,
                           @RequestParam String startDate,
                           @RequestParam String endDate,
                           @RequestParam String selectedColor) {

        Calendar event = new Calendar();
        event.setGroupNo(111); // 임시로 설정
        event.setCContent(eventContent);
        event.setCStartDate(LocalDate.parse(startDate));
        event.setCEndDate(LocalDate.parse(endDate));
        event.setCColor(selectedColor);

        cs.saveDiary(event);

        return "일정이 등록되었습니다";
    }
}
