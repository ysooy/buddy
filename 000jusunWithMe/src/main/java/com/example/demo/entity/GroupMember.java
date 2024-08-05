package com.example.demo.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "groupMember")
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberNo;

    @Column(nullable = false)
    private long groupNo;

    @Column(nullable = false)
    private long userNo;

    @Column(nullable = false)
    private int leader = 0;  // 기본값 0으로 설정. 리더일 때만 1


}
