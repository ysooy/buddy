package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NotiNo", nullable = false)
    private long notiNo;

    @Column(name = "userNo", nullable = false)
    private long userNo;

    @Column(name = "groupNo", nullable = false)
    private long groupNo;

    @Column(name = "cOrP", nullable = false)
    private String cOrP;

    @Column(name = "checked", nullable = false)
    private String checked = "X";

    @Column(name = "notiTime", nullable = false)
    private LocalDateTime notiTime;

    @Column(name = "postNo", nullable = false)
    private long postNo;
    
    @Column(name = "comNo", nullable = true)
    private Long comNo;
    
    @Transient // 데이터베이스에 저장되지 않는 필드로 설정했음
    private String userName;

    @Transient
    private String userProfilePhoto;
    
    @Transient     
    private String formattedDate; // 포맷된 날짜를 저장하기 위한 필드
    
//    @Transient
//    private List<String> postFname;
}
