package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.PostRepository;
import com.example.demo.dao.UsersRepository;
import com.example.demo.dto.PostDTO;
import com.example.demo.entity.Post;
import com.example.demo.entity.Users;

@Service
public class PostService {

	@Autowired
	private PostRepository pr;
	
	@Autowired
	private UsersRepository ur;
	
	//게시글 불러오기
	public List<PostDTO> listPost(long feedNo){
		 List<Post> posts =  pr.findByFeedNo(feedNo);
		 List<PostDTO> postDTOs = new ArrayList<>();
		 
		 for(Post post: posts) {
			 Users user = ur.findByUserNo(post.getUserNo());
			 PostDTO postDTO = new PostDTO(
					    post.getPostNo(),
					    post.getPostContent(),
					    post.getPostTime(),
					    post.getPostFname(),
					    post.getFeedNo(),
					    user.getUsername(),
					    user.getProfilePhoto()
					);
			 postDTOs.add(postDTO);
		 }
		 return postDTOs;
	}
}
