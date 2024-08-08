package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.GroupTable;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Post;
import com.example.demo.entity.Users;
import com.example.demo.service.NotificationService;
import com.example.demo.service.PostService;

import jakarta.servlet.http.HttpSession;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService ns;

    @GetMapping("/notification")
    public String getNotifications(Model model, HttpSession session) {
        // 세션에서 사용자와 그룹 정보를 가져옴
        Long userNo = ((Users) session.getAttribute("loginUser")).getUserNo();
        Long groupNo = ((GroupTable) session.getAttribute("selectedGroup")).getGroupNo();

        // 그룹에서 현재 로그인중인 사용자를 제외한 다른 정보 가져오기
        List<Notification> notifications = ns.getNotificationsByGroupExUser(groupNo, userNo);
        model.addAttribute("notifications", notifications);
        return "notification/notification";
    }

    
//    @GetMapping("/notification")
//    public String getNotifications(Model model, @RequestParam(required = false) Long userNo, @RequestParam(required = false) Integer groupNo) {
//        if (userNo != null && groupNo != null) {	// 유저정보, 그룹번호
//            List<Notification> notifications = ns.getNotificationsByUserAndGroup(userNo, groupNo);
//            model.addAttribute("notifications", notifications);
//        } else {
//            model.addAttribute("notifications", List.of()); // 유저,그룹번호가 제공되지 않더라도 뷰에서 속성 유지(null 예외 방지용)
//        }
//        return "notification/notification";
//    }


}
