package com.example.demo.controller;

import com.example.demo.entity.Calendar;
import com.example.demo.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
        List<Calendar> calendarSchedules = cs.getAllDiary();
        model.addAttribute("calendarSchedules", calendarSchedules);
        return "calendar/calendar";
    }

    // 등록되어있는 json형식의 일정 데이터를 반환(ajax요청)
    @GetMapping("/calendar/schedules")
    @ResponseBody
    public List<Map<String, Object>> getSchedules() {
        List<Calendar> calendarSchedules = cs.getAllDiary();
        List<Map<String, Object>> schedules = new ArrayList<>();
        for (Calendar schedule : calendarSchedules) {
            Map<String, Object> scheduleData = new HashMap<>();
            scheduleData.put("title", schedule.getCContent());
            scheduleData.put("start", schedule.getCStartDate().toString());
            scheduleData.put("end", schedule.getCEndDate().toString());
            scheduleData.put("color", schedule.getCColor());
            scheduleData.put("calendarNo", schedule.getCalendarNo());
            schedules.add(scheduleData);
        }
        return schedules;
    }

    // 캘린더 일정 등록
    @PostMapping("/calendar/add")
    @ResponseBody
    public Map<String, Object> addSchedule(@RequestParam String cContent,
                                           @RequestParam String startDate,
                                           @RequestParam String endDate,
                                           @RequestParam String selectedColor) {
        Calendar schedule = new Calendar();
        schedule.setGroupNo(111); // 임시로 설정
        schedule.setCContent(cContent);
        schedule.setCStartDate(LocalDate.parse(startDate));
        schedule.setCEndDate(LocalDate.parse(endDate));
        schedule.setCColor(selectedColor);

        Calendar savedSchedule = cs.saveDiary(schedule);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "일정이 등록되었습니다");
        response.put("calendarNo", savedSchedule.getCalendarNo());

        return response;
    }

    // 캘린더 일정 수정
    @PostMapping("/calendar/update")
    @ResponseBody
    public String updateSchedule(@RequestParam Integer calendarNo,
                                 @RequestParam String cContent,
                                 @RequestParam String startDate,
                                 @RequestParam String endDate,
                                 @RequestParam String selectedColor) {
        Calendar schedule = cs.getDiaryById(calendarNo);
        if (schedule != null) {
            schedule.setCContent(cContent);
            schedule.setCStartDate(LocalDate.parse(startDate));
            schedule.setCEndDate(LocalDate.parse(endDate));
            schedule.setCColor(selectedColor);
            cs.saveDiary(schedule);
            return "일정이 수정되었습니다.";
        } else {
            return "일정을 찾을 수 없습니다.";
        }
    }

    // 캘린더 일정 삭제
    @GetMapping("/calendar/delete/{groupNo}/{calendarNo}")
    @ResponseBody
    public String deleteSchedule(@PathVariable Integer groupNo,
                                 @PathVariable Integer calendarNo) {
        Calendar schedule = cs.getDiaryById(calendarNo);
        if (schedule != null && schedule.getGroupNo().equals(groupNo)) {
            cs.deleteDiary(calendarNo);
            return "일정이 삭제되었습니다.";
        } else {
            return "일정을 찾을 수 없습니다.";
        }
    } 

}
