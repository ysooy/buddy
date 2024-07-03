package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FeedController {

	@GetMapping("/feed/feed")
    public void feedView() {
		
	};
	
	@GetMapping("/feed/feed2")
    public void feed2View() {
		
	};
	
	@GetMapping("/feed/feedWrite")
    public void feedWriteView() {
		
	};
}
