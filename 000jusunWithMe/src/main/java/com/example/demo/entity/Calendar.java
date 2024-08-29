package com.example.demo.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "calendar")
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendarNo")
    private long calendarNo;

    @Column(name = "groupNo")
    private long groupNo;

    @Column(name = "cStartDate")
    @JsonProperty("start")
    private LocalDate cStartDate;

    @Column(name = "cEndDate")
    @JsonProperty("end")
    private LocalDate cEndDate;

    @Column(name = "cContent")
    @JsonProperty("title")
    private String cContent;

    @Column(name = "cColor")
    @JsonProperty("color")
    private String cColor;
}

