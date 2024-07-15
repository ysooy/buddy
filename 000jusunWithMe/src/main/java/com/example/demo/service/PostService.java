package com.example.demo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.example.demo.dao.CommentRepository;
import com.example.demo.dao.PostRepository;
import com.example.demo.dao.UsersRepository;
import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.PostDTO;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.Users;

import jakarta.servlet.http.HttpSession;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired // 파일 경로찾기용
	private ResourceLoader resourceLoader;
    @Autowired
    private MongoTemplate mongoTemplate;

    //게시글 리스팅 
    public List<PostDTO> listPost(long feedNo) {
        List<Post> posts = postRepository.findByFeedNo(feedNo);
        List<PostDTO> postDTOs = new ArrayList<>();
        
        //특정 피드에 해당하는 게시글들을 postDTO에 넣고, postDTOs에 태움
        for (Post post : posts) {
            Users user = userRepository.findByUserNo(post.getUserNo());
            List<Comment> comments = commentRepository.findByPostNo(post.getPostNo());
            List<CommentDTO> commentDTOs = new ArrayList<>();
            
            //특정 게시글에 해당하는 댓글들을 commentDTO에 넣고, commentDTOs에 태움
            for (Comment comment : comments) {
                Users commentWriter = userRepository.findByUserNo(comment.getUserNo());
                CommentDTO commentDTO = new CommentDTO(
                    comment.getComNo(),
                    commentWriter.getUsername(),
                    commentWriter.getProfilePhoto(),
                    comment.getComTime(),
                    comment.getComContent()
                );
                commentDTOs.add(commentDTO);
            }

            PostDTO postDTO = new PostDTO(
                post.getPostNo(),
                post.getPostContent(),
                post.getPostTime(),
                post.getPostFname(),
                post.getFeedNo(),
                user.getUsername(),
                user.getProfilePhoto(),
                commentDTOs
            );

            postDTOs.add(postDTO);
        }
        return postDTOs;
    }

    //게시글 등록(save)
    public Post savePost(Post post, MultipartFile[] photoUpload) {
    	//글 등록 시각: 현재시각으로 설정 
    	post.setPostTime(new Date());
    	
    	//게시글 번호: 1씩 늘어나도록
    	post.setPostNo(getNextPostNo());
    	
    	//피드 번호:**피드 구상 후 수정 필요!!**
    	post.setFeedNo(1);
    	
    	//사진
    	List<String> postFname = uploadPhotos(photoUpload);
    	post.setPostFname(postFname);
    	
    	return postRepository.save(post);
    }
    
    
    
    
    
    
    //---- 이하 메소드들 -----
    //사진(여러 장) 등록 메소드
    public List<String> uploadPhotos(MultipartFile[] photoupload) {
    	List<String> postFname = new ArrayList<>();
    	String path = "src/main/resources/static/images";
    	for(MultipartFile photo: photoupload) {
    		String fname = photo.getOriginalFilename();
    		if(fname != null && !fname.equals("")) {
    			try {
					byte[] photoData = photo.getBytes();
					FileOutputStream fos = new FileOutputStream(path+"/"+fname);
					fos.write(photoData);
					fos.close();
					postFname.add(fname);
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
    	}
    	return postFname;
    }
    
    //게시글번호 1씩 증가하게 만드는 메소드
    public long getNextPostNo() {
    	Query query = new Query();
    	query.with(Sort.by(Sort.Direction.DESC, "postNo"));
    	query.limit(1);
    	Post lastPost = mongoTemplate.findOne(query, Post.class);
    	long newPostNo = (lastPost != null) ? lastPost.getPostNo()+1 : 1 ;
    	return newPostNo; 
    }
}