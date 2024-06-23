package com.example.demo.service;

import com.example.demo.dao.ChatCollectionRepository;
import com.example.demo.vo.ChatCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatCollectionRepository dao;

    public List<ChatCollection> getList() {
        return dao.findAll();
    }
}
