package com.example.demo.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.CommentRepository;
import com.example.demo.dao.NotificationRepository;
import com.example.demo.dao.PostRepository;
import com.example.demo.entity.Comment;
import com.example.demo.entity.GroupTable;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Post;

import jakarta.servlet.http.HttpSession;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository nr;
    @Autowired
    private PostRepository pr;
    @Autowired
    private CommentRepository commentRepository; // CommentRepository 주입

    // 모든 알림 목록
    public List<Notification> getAllNotifications() {
        return nr.findAll();
    }

    // 특정 사용자의 특정 그룹에 해당하는 알림 목록
    public List<Notification> getNotificationsByUserAndGroup(Long userNo, int groupNo) {
        return nr.findByUserNoAndGroupNo(userNo, groupNo);
    }

    // 알림 확인 시 checked 상태 변경 (default X > O)
    public void markNotificationsAsChecked(Long userNo, int groupNo) {
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
        Comment savedComment = commentRepository.save(comment); // 주입된 인스턴스를 사용

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
            // 예외 처리를 하고 싶지 않다면 로그를 남기거나 별도의 처리 없이 진행할 수 있습니다.
            System.out.println("Post not found for post number: " + savedComment.getPostNo());
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
}
