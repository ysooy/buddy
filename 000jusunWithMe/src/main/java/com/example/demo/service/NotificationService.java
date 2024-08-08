package com.example.demo.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.CommentRepository;
import com.example.demo.dao.NotificationRepository;
import com.example.demo.dao.PostRepository;
import com.example.demo.dao.UsersRepository;
import com.example.demo.entity.Comment;
import com.example.demo.entity.GroupTable;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Post;
import com.example.demo.entity.Users;

import jakarta.servlet.http.HttpSession;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository nr;
    @Autowired
    private PostRepository pr;
    @Autowired
    private CommentRepository cr;
    @Autowired
    private UsersRepository ur;

    // 모든 알림 목록
    public List<Notification> getAllNotifications() {
        return nr.findAll();
    }

//    // 특정 그룹에 속한 모든 사용자의 알림을 가져오는 메서드
//    public List<Notification> getNotificationsByGroup(Long groupNo) {
//        List<Notification> notifications = nr.findByGroupNo(groupNo);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        for (Notification notification : notifications) {
//            // 사용자 이름과 프로필 사진 설정
//            Users user = ur.findById(notification.getUserNo()).orElse(null);
//            if (user != null) {
//                notification.setUserName(user.getUsername());
//                notification.setUserProfilePhoto(user.getProfilePhoto());
//            }
//            // 날짜를 원하는 형식으로 포맷하여 추가
//            LocalDate notiTime = notification.getNotiTime();
//            notification.setFormattedDate(notiTime.format(formatter));
//        }
//        return notifications;
//    } 
    
    
    // 그룹에서 현재 로그인중인 사용자를 제외한 다른 정보 가져오기
    public List<Notification> getNotificationsByGroupExUser(Long groupNo, Long userNo) {
        List<Notification> notifications = nr.findByGroupNoAndUserNoNot(groupNo, userNo);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Notification notification : notifications) {
            // 사용자 이름과 프로필 사진 설정
            Users user = ur.findById(notification.getUserNo()).orElse(null);
            if (user != null) {
                notification.setUserName(user.getUsername());
                notification.setUserProfilePhoto(user.getProfilePhoto());
            }
            // 날짜형식 포맷 바꾸기(post와 comment 날짜 형식이 다름)             
            LocalDate notiTime = notification.getNotiTime();
            notification.setFormattedDate(notiTime.format(formatter));
        }
        return notifications;
    }


    // 알림 확인 시 checked 상태 변경 (default X > O)
    public void markNotificationsAsChecked(Long userNo, Long groupNo) {
        List<Notification> notifications = nr.findByUserNoAndGroupNo(userNo, groupNo);
        // 'X'로 설정된 알림만 'O'로 변경 (알림창 확인 시 O로 업데이트 후 추가 업데이트 없음)
        notifications.forEach(notification -> {
            if ("X".equals(notification.getChecked())) {
                notification.setChecked("O");
            }
        });

        nr.saveAll(notifications);
    }

    // 게시글 등록 시 알림을 저장하는 메서드
    public void saveNotification(Post post) {
        Notification notification = new Notification();
        notification.setUserNo(post.getUserNo());
        notification.setGroupNo(post.getGroupNo());

        // cOrP값 설정( 글만 있으면 '피드(글)', 사진이 포함되면 '피드(사진)'
        if (post.getPostFname() == null || post.getPostFname().isEmpty()) {
            notification.setCOrP("피드(글)");
        } else {
            notification.setCOrP("피드(사진)");
        }

        // 알림창 체크유무 (기본 X)
        notification.setChecked("X");
        notification.setNotiTime(post.getPostTime().toLocalDate());
        notification.setPostNo(post.getPostNo());
        notification.setComNo(null); // 댓글이 없는 경우 null로 설정

        nr.save(notification);
    }

    // 댓글 등록 시 알림 저장
    public void saveCommentNotification(Comment comment) {
        Comment savedComment = cr.save(comment); // 주입된 인스턴스를 사용

        if (savedComment.getComNo() == null) {
            throw new IllegalStateException("Comment could not be saved.");
        }
         
        Notification notification = new Notification();
        notification.setUserNo(savedComment.getUserNo());
        notification.setPostNo(savedComment.getPostNo());
        notification.setComNo(savedComment.getComNo());

        Post post = pr.findByPostNo(savedComment.getPostNo());
        if (post != null) {
            notification.setGroupNo(post.getGroupNo()); // Post에서 groupNo 가져오기
        } else {
            System.out.println("해당 포스트 번호에 대한 Post를 찾을 수 없습니다: " + savedComment.getPostNo());
        }
        // cOrP 값 설정 (댓글에 대한 알림임을 표시)
        notification.setCOrP("댓글");
        notification.setChecked("X");

        // Date를 LocalDate로 변환하여 notiTime에 설정
        Date comTime = savedComment.getComTime();
        if (comTime != null) {
            Instant instant = comTime.toInstant();
            ZoneId zoneId = ZoneId.systemDefault(); // 기본 시스템 시간대를 사용
            LocalDate localDate = instant.atZone(zoneId).toLocalDate();
            notification.setNotiTime(localDate);
        } else {
            notification.setNotiTime(LocalDate.now()); // comTime이 null일 경우 현재 날짜를 사용
        }

        nr.save(notification);
    }
    
    // notiTime 형식 지정(feedDate(LocalDate)와 comTime(Date) 형식 변환 위함)
    public LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant()
                   .atZone(ZoneId.systemDefault())
                   .toLocalDate();
    }
    
}
