
package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        // 로그인 상태 확인
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null) {
            model.addAttribute("errorMessage", "사용자 정보를 불러올 수 없습니다.");
            return "redirect:/login"; // 로그인 페이지로 리디렉션
        }

        // 그룹 정보 확인
        GroupTable gt = (GroupTable) session.getAttribute("selectedGroup");

        if (gt == null) {
            model.addAttribute("errorMessage", "그룹 정보를 불러올 수 없습니다. 그룹을 선택해주세요.");
            return "redirect:/firstpage/firstpage"; // 그룹 선택 페이지로 리디렉션
        } else if (!gt.getGroupNo().equals(groupNo)) {
            // 세션에 저장된 그룹과 요청된 그룹 번호가 다른 경우 그룹 정보를 업데이트
            gt = new GroupTable();
            gt.setGroupNo(groupNo);
            session.setAttribute("selectedGroup", gt);
        }

        // 사용자와 그룹 정보를 세션에 저장
        session.setAttribute("loginUser", user);
        session.setAttribute("selectedGroup", gt);

        // 알림 목록을 가져와서 모델에 추가
        List<Notification> notifications = ns.getNotificationsByGroupExUser(groupNo, user.getUserNo());
        model.addAttribute("notifications", notifications);
        model.addAttribute("selectedGroup", gt);  // selectedGroup을 모델에 추가

        return "notification/notification"; // 알림 페이지로 이동
    }



    // 알림 확인 처리용 POST 엔드포인트 추가
    @PostMapping("/notification/check")
    public ResponseEntity<String> checkNotifications(@RequestParam Long groupNo, HttpSession session) {
    	// http 응답 반환!
        Users user = (Users) session.getAttribute("loginUser");	// 로그인된 사용자 정보
        if (user == null) {	// 로그인하지 않았거나 세션이 끊긴 경우
            return ResponseEntity.status(401).body("unauthorized");	// 권한없음
        }
        
        // 사용자 알림 업데이트(X에서 O로 변경)
        ns.markNotificationsAsChecked(user.getUserNo(), groupNo);
        return ResponseEntity.ok("성공적으로 업데이트 되었습니다.");
    }

}
