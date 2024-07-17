package com.example.demo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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
    	post.setPostTime(LocalDateTime.now());
    	
    	//게시글 번호: 1씩 늘어나도록
    	post.setPostNo(getNextPostNo());
    	System.out.println("PostService의 photoupload: "+photoUpload[0].getOriginalFilename());
    	//사진
    	List<String> postFname = uploadPhotos(photoUpload);
    	System.out.println("ps의 postFname: "+postFname); //얘가 자꾸 없어
    	post.setPostFname(postFname);
    	
    	return postRepository.save(post);
    }
    
    
    
    
    
    
    //---- 이하 메소드들 -----
    //사진(여러 장) 등록 메소드
    public List<String> uploadPhotos(MultipartFile[] photoupload) {
        List<String> postFname = new ArrayList<>();
        String path = "src/main/resources/static/images";

        for (MultipartFile photo : photoupload) {
            String fname = photo.getOriginalFilename();
            System.out.println("uploadPhotos메소드 fname: " + fname);

            if (fname != null && !fname.isEmpty()) {
                System.out.println("if문 안에 들어왔음!");
                try {
                    // 고유한 파일 이름 생성
                    String uniqueFname = System.currentTimeMillis() + "_" + fname;
                    FileOutputStream fos = new FileOutputStream(path + "/" + uniqueFname);
                    fos.write(photo.getBytes());
                    fos.close();
                    postFname.add(uniqueFname);
                    System.out.println("uploadPhoto메소드 postFname: " + postFname);
                } catch (IOException e) {
                    System.err.println("파일 처리 중 오류: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("기타 오류: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("파일 이름이 유효하지 않음: " + fname);
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