package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavController {

	//nav 1 홈 클릭시
	@GetMapping("/groupHome/firstpage")
    public void firstpageView() {
		
	};
}
