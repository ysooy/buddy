package com.example.demo.controller;

import com.example.demo.entity.Calendar;
import com.example.demo.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Map<String, Object>> getEvents() {
        List<Calendar> calendarEvents = cs.getAllDiary();
        List<Map<String, Object>> events = new ArrayList<>();
        for (Calendar event : calendarEvents) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("title", event.getCContent());
            eventData.put("start", event.getCStartDate().toString());
            eventData.put("end", event.getCEndDate().toString());
            eventData.put("color", event.getCColor());
            eventData.put("calendarNo", event.getCalendarNo()); // 추가된 부분
            events.add(eventData);
        }
        return events;
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

    // 캘린더 일정 수정
    @PostMapping("/calendar/update")
    @ResponseBody
    public String updateEvent(@RequestParam Integer calendarNo,
                              @RequestParam String eventContent,
                              @RequestParam String startDate,
                              @RequestParam String endDate,
                              @RequestParam String selectedColor) {

        Calendar event = cs.getDiaryById(calendarNo);
        if (event != null) {
            event.setCContent(eventContent);
            event.setCStartDate(LocalDate.parse(startDate));
            event.setCEndDate(LocalDate.parse(endDate));
            event.setCColor(selectedColor);
            cs.saveDiary(event);
            return "일정이 수정되었습니다.";
        } else {
            return "일정을 찾을 수 없습니다.";
        }
    }
}
