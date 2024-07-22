package com.example.demo.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Calendar;

public interface CalendarRepository extends JpaRepository<Calendar, Integer> {


}
