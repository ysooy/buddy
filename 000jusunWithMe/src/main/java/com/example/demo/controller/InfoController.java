package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {

	@GetMapping("/myInfo/myInfo")
    public void myInfoView() {
		
	};
	
	@GetMapping("/groupInfo/groupInfo")
    public void groupInfoView() {
		
	};
	
}
