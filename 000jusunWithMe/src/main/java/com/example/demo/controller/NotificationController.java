package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/notification/{groupNo}")
    public String getNotifications(@PathVariable Long groupNo, Model model, HttpSession session) {
    	// 로그인, 그룹 유지
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null) {
            model.addAttribute("errorMessage", "사용자 정보를 불러올 수 없습니다.");
            return "redirect:/login"; // 로그인 페이지로
        }
        
        GroupTable gt = (GroupTable) session.getAttribute("selectedGroup");
        
        if (gt == null) {
            model.addAttribute("errorMessage", "그룹 정보를 불러올 수 없습니다. 그룹을 선택해주세요.");
            return "redirect:/firstpage/firstpage";
        } else if (!gt.getGroupNo().equals(groupNo)) {
            // 세션의 그룹과 현재 요청된 그룹이 다를 경우 그룹 정보를 업데이트
            gt = new GroupTable();
            gt.setGroupNo(groupNo);
            session.setAttribute("selectedGroup", gt);
        }
        
        // 세션에 사용자,그룹 저장
        session.setAttribute("loginUser", user);
        session.setAttribute("selectedGroup", gt);
        
        // 세션에서 사용자와 그룹 정보를 가져옴
        // 위에서 세션저장 해서 아래 코드는 사용X
//        Long userNo = ((Users) session.getAttribute("loginUser")).getUserNo();
//        Long groupNo = ((GroupTable) session.getAttribute("selectedGroup")).getGroupNo();
        Long userNo = user.getUserNo();
        groupNo = gt.getGroupNo();  

        // 그룹에서 현재 로그인중인 사용자를 제외한 다른 정보 가져오기
        List<Notification> notifications = ns.getNotificationsByGroupExUser(groupNo, userNo);
        model.addAttribute("notifications", notifications);
        return "notification/notification";
    }


}
