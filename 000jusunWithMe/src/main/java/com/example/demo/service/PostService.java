package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.CommentRepository;
import com.example.demo.dao.PostRepository;
import com.example.demo.dao.UsersRepository;
import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.PostDTO;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.Users;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

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
}