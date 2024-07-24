package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Feed;
import com.example.demo.entity.Post;
import com.example.demo.service.CommentService;
import com.example.demo.service.FeedService;
import com.example.demo.service.PostService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FeedController {

	
	@Autowired
	private PostService ps;
	@Autowired
	private CommentService cs;
	@Autowired
	private FeedService fs;
	
	@GetMapping("/feed/feed")
    public void feedView(Model model) {
		model.addAttribute("feeds",fs.listFeed(1));
	};
	
	@GetMapping("/feed/post/{feedNo}")
    public String postView(@PathVariable long feedNo, Model model) {
		model.addAttribute("posts",ps.listPost(feedNo));  //테스트용: 1번피드의 포스트들리스트
		return "/feed/post";
	};
	
	@GetMapping("/feed/postWrite")
    public void postWriteView() {
		
	};
	
	//게시물 등록
	@PostMapping("/feed/postWrite")
    public String postWrite(Post post, MultipartFile[] photoUpload) {
		//**해당 그룹은 현재 session유지된 걸로 가져올 예정!!추후 수정 필요함
		long groupNo = 1;
		//해당그룹에서 해당날짜에 첫 게시물일 시(피드 검색) 피드에 저장
		//첫 게시물인지 확인
		Feed oldFeed = fs.checkFeedExists(groupNo, post.getPostUserDefDate());
		if(oldFeed==null){ //기존 피드가 없다면
			Feed newFeed = new Feed(); //새 피드 생성
			
			//사용자 지정 날짜로 날짜 등록
			newFeed.setFeedDate(post.getPostUserDefDate());
			
			//배경사진으로 사용할 사진파일
			String firstPhoto = null;
			firstPhoto = photoUpload[0].getOriginalFilename();
			MultipartFile mpfForLocation = null;
			mpfForLocation = photoUpload[0];
			if(firstPhoto!=null && !firstPhoto.equals("")) {
				newFeed.setFeedPhoto(firstPhoto);	
				String location = "";
				location = fs.getLocation(mpfForLocation);
				newFeed.setFeedLocation(location);
			}
			
			//그룹번호
			newFeed.setGroupNo(groupNo); 
			
			long newFeedNo =fs.getNextFeedNo();
			//feed에도 새 feedNo부여
			newFeed.setFeedNo(newFeedNo);
			
			fs.saveFeed(newFeed);
			
			//post에 새 feedNo 부여
			post.setFeedNo(newFeedNo);
		}else {
			//이미 feed가 있는 경우 기존 feedNo 부여 
			post.setFeedNo(oldFeed.getFeedNo());
		}
		ps.savePost(post, photoUpload);
		return "redirect:/feed/post/"+post.getFeedNo(); 
	};
	
	//댓글 등록
	@PostMapping("/feed/commentWrite")
	public String commentWrite(Comment comment) {
		long feedNo = cs.findByPostNo(comment.getPostNo()).getFeedNo();
		cs.saveComment(comment);
		return "redirect:/feed/post/"+feedNo;
	}
}
