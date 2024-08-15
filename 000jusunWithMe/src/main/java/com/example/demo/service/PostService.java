package com.example.demo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
import com.example.demo.dao.NotificationRepository;
import com.example.demo.dao.PostRepository;
import com.example.demo.dao.UsersRepository;
import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.PostDTO;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Feed;
import com.example.demo.entity.Notification;
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
    
    @Autowired
    private NotificationRepository notificationRepository;

 
	//postNo로 post 찾기
	public Post findByPostNo(long postNo) {
		return postRepository.findByPostNo(postNo);
	}
    //게시글 리스팅 
    public List<PostDTO> listPost(long feedNo) {
        List<Post> posts = postRepository.findByFeedNo(feedNo);
        // 역순으로 정렬
        posts.sort(Comparator.comparingLong(Post::getPostNo).reversed());
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
                    commentWriter.getUserNo(),
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
                post.getPostUserDefDate(),
                post.getPostTime(),
                post.getPostFname(),
                post.getFeedNo(),
                user.getUserNo(),
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
    	
    	//사진들
    	List<String> postFname = uploadPhotos(photoUpload);
    	post.setPostFname(postFname);
    	
    	return postRepository.save(post);
    }
    
    // 게시글 등록 시 알림창 저장 
    public void saveNotification(Post post) {
    	Notification nf = new Notification();
    	nf.setUserNo(post.getUserNo());
    	nf.setGroupNo(post.getGroupNo());
    	
    	// cOrP값 설정( 글만 있으면 '피드(글)', 사진이 포함되면 '피드(사진)'
    	if(post.getPostFname() == null || post.getPostFname().isEmpty()) {
    		nf.setCOrP("피드(글)");
    	}else {
    		nf.setCOrP("피드(사진)");
    	}
    	
    	// 알림창 체크유무 (기본 X)
    	nf.setChecked("X");
    	nf.setNotiTime(post.getPostTime());
    	nf.setPostNo(post.getPostNo());
    	
    	notificationRepository.save(nf);
    	
    }
    
    
    //게시글 수정(save)
    public Post updatePost(Post post) {
    	return postRepository.save(post);
    }
    
    //게시 삭제
    public void deletePost(Post post) {
    	postRepository.delete(post);
    }
    
    //해당 피드의 마지막post인지 확인하기: feedNo로 post찾아서 그 수 반환 
    public int countPostByFeedNo(long feedNo) {
    	List<Post> posts = postRepository.findByFeedNo(feedNo);
    	return posts.size();
    	
    }

    
    //---- 이하 메소드들 -----
    //사진 삭제 메소드
    public void deletePhotos(Post p) {
    	String path = "src/main/resources/static/images";
    	List<String> postFname = new ArrayList<>();
    	postFname = p.getPostFname();
        for (String fname : postFname) {

            if (fname != null && !fname.isEmpty()) {
                try {
                	File file = new File(path+"/"+fname);
          			file.delete();	
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } 
        }
    }
    
    //사진(여러 장) 등록 메소드
    public List<String> uploadPhotos(MultipartFile[] photoupload) {
        List<String> postFname = new ArrayList<>();
        String path = "src/main/resources/static/images";

        for (MultipartFile photo : photoupload) {
            String fname = photo.getOriginalFilename();

            if (fname != null && !fname.isEmpty()) {
                try {
                    FileOutputStream fos = new FileOutputStream(path + "/" + fname);
                    fos.write(photo.getBytes());
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