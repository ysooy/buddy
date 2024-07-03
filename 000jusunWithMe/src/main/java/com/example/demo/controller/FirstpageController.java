package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FirstpageController {
	
	@GetMapping("/firstpage/firstpage")
    public void firstpageView() {
    }
	
	@GetMapping("/firstpage/enterGroup")
    public void enterGroupView() {
    }
	
	@GetMapping("/firstpage/makeGroup")
    public void makeGroupView() {
    }
	
}
