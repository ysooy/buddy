package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FeedController {

	@GetMapping("/feed/feed")
    public void feedView() {
		
	};
	
	@GetMapping("/feed/post")
    public void postView() {
		
	};
	
	@GetMapping("/feed/postWrite")
    public void postWriteView() {
		
	};
}
