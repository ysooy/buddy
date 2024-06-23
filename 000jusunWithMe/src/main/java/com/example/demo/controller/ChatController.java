package com.example.demo.controller;

import com.example.demo.dao.ChatCollectionRepository;
import com.example.demo.service.ChatService;
import com.example.demo.vo.ChatCollection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @Autowired
    private ChatService cs;

    @GetMapping("/chatTest")
    public String list(Model model) {
        List<ChatCollection> dataList = cs.getList();
        System.out.print("데이터"+dataList);
        model.addAttribute("dataList", dataList);
        return "chatTest";
    }
}