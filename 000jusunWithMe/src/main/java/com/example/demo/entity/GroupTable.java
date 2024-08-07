package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "groupTable")
public class GroupTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupNo;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false)
    private LocalDate groupDate;

    @Column(nullable = false, unique = true)
    private String inviteCode;

    @Column(nullable = true)
    private String groupProfilePhoto;

}
