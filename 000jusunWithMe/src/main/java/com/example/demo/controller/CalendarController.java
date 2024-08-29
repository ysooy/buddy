package com.example.demo.controller;

import com.example.demo.entity.Calendar;
import com.example.demo.service.CalendarService;
import com.example.demo.service.GroupService;

import jakarta.servlet.http.HttpSession;

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
    
    @Autowired
    private GroupService gs;

    // 페이지가 로드될 때 캘린더 일정 데이터 모델에 추가한 후 뷰 반환
    @GetMapping("/calendar/calendar/{groupNo}")
    public String calendar(@PathVariable("groupNo") long groupNo, Model model, HttpSession session) {

        // 그룹 번호와 이름을 로그로 출력하여 올바르게 설정되었는지 확인
        System.out.println("Received groupNo: " + groupNo);    	
    	
        // 세션에서 그룹 번호와 이름을 가져옴
        Long sessionGroupNo = (Long) session.getAttribute("selectedGroupNo");
        String selectedGroupName = (String) session.getAttribute("selectedGroupName");
        
        
        // 세션에 그룹 번호와 이름이 없는 경우 기본적으로 URL에서 받은 groupNo를 사용
        if (sessionGroupNo == null || selectedGroupName == null) {
            // groupService를 통해 groupNo에 해당하는 그룹 이름을 가져옴
            selectedGroupName = gs.getGroupNameById(groupNo); // 실제로 그룹 이름을 가져오는 로직
            session.setAttribute("selectedGroupNo", groupNo);
            session.setAttribute("selectedGroupName", selectedGroupName);
        } else {
            groupNo = sessionGroupNo; // 세션에 값이 있으면 그것을 사용
        }  
    	
        List<Calendar> calendarSchedules = cs.getDiaryByGroupNo(groupNo); // 특정 그룹의 일정만
        model.addAttribute("calendarSchedules", calendarSchedules);
        model.addAttribute("groupNo", session.getAttribute("selectedGroupNo"));
        model.addAttribute("groupName", selectedGroupName);  
        
        System.out.println("Model groupNo: " + session.getAttribute("selectedGroupNo"));
        System.out.println("Model groupName: " + selectedGroupName);
        
        return "calendar/calendar";
    }

    // 등록되어있는 json형식의 일정 데이터를 반환(ajax요청)
    @GetMapping("/calendar/{groupNo}/schedules")
    @ResponseBody
    public List<Map<String, Object>> getSchedules(@PathVariable long groupNo) {
        List<Calendar> calendarSchedules = cs.getDiaryByGroupNo(groupNo);
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
    public Map<String, Object> addSchedule(@RequestParam Long groupNo,
            @RequestParam String cContent,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String selectedColor) {
    	
		Calendar schedule = new Calendar();        
		schedule.setGroupNo(groupNo);
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
    @PostMapping("/calendar/update/{groupNo}")
    @ResponseBody
    public String updateSchedule(@RequestParam long calendarNo,
    							 @RequestParam long groupNo,
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
    public String deleteSchedule(@PathVariable long groupNo,
                                 @PathVariable long calendarNo) {
        Calendar schedule = cs.getDiaryById(calendarNo);
        if (schedule != null && schedule.getGroupNo() == groupNo) {
            cs.deleteDiary(calendarNo);
            return "일정이 삭제되었습니다.";
        } else {
            return "일정을 찾을 수 없습니다.";
        }
    } 
    
    

}
