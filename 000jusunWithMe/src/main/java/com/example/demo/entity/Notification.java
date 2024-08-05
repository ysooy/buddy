package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

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
    private LocalDate notiTime;

    @Column(name = "postNo", nullable = false)
    private long postNo;
    
    @Column(name = "comNo", nullable = true)
    private Long comNo;
}
