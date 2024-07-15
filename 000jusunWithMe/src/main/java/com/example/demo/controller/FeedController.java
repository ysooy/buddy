package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Post;
import com.example.demo.service.PostService;

@Controller
public class FeedController {

	
	@Autowired
	private PostService ps;
	
	@GetMapping("/feed/feed")
    public void feedView() {
		
	};
	
	@GetMapping("/feed/post")
    public void postView(Model model) {
		model.addAttribute("posts",ps.listPost(1));  //테스트용: 1번피드의 포스트들리스트
	};
	
	@GetMapping("/feed/postWrite")
    public void postWriteView() {
		
	};
	
	@PostMapping("/feed/postWrite")
    public String postWrite(Post post, MultipartFile[] photoUpload) {
		ps.savePost(post, photoUpload);
		return "redirect:/feed/post";
	};
}
