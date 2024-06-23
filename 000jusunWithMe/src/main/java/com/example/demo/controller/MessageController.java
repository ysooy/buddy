package com.example.demo.controller;

import com.example.demo.service.MessageService;
import com.example.demo.vo.MessageCollection;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MessageController {

    @Autowired
    private MessageService ms;

    @GetMapping("/chatTest")
    public String list(Model model) {
        List<MessageCollection> dataList = ms.getList();
        System.out.print("데이터"+dataList);
        model.addAttribute("dataList", dataList);
        return "chatTest";
    }
}