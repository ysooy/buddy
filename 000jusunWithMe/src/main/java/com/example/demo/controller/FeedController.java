package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class FeedController {

	
	@Autowired
	private PostService ps;
	@Autowired
	private CommentService cs;
	@Autowired
	private FeedService fs;
	
	@GetMapping("/feed/feed/{groupNo}")
    public String feedView(Model model, @PathVariable long groupNo) {
		model.addAttribute("feeds",fs.listFeed(groupNo));
		return "/feed/feed";
	};
	
	@GetMapping("/feed/post/{feedNo}")
    public String postView(@PathVariable long feedNo, Model model) {
		model.addAttribute("posts",ps.listPost(feedNo));  //테스트용: 1번피드의 포스트들리스트
		return "/feed/post";
	};
	
	@GetMapping("/feed/postWrite")
    public void postWriteView() {
		
	};
	
	//게시물 삭제
	@GetMapping("/feed/deletePost/{postNo}")
	public String deletePost(@PathVariable("postNo") long postNo) {
		int groupNo = 1; //일단 1로 해놓겠음!!!!!******세션연결해서 가져와야함!!!!
		Post p = ps.findByPostNo(postNo);
		String landing = "redirect:/feed/post/"+p.getFeedNo();
		//여기 달린 댓글 있으면 그것도 삭제
		List<Comment> relatedComments = null; 
		relatedComments= cs.findCommentByPostNo(postNo);
		if (relatedComments !=null) {
			for(Comment c: relatedComments) {
				cs.deleteComment(c.getComNo());
			}
		}
		ps.deletePost(p);
		ps.deletePhotos(p);
		
		//해당 피드의 마지막 게시글을 삭제하는 경우 피드 삭제
		long feedNo = p.getFeedNo();
		int postCnt = ps.countPostByFeedNo(feedNo);
		if(postCnt == 0) {
			fs.deleteFeed(feedNo);
			//해당 피드가 사라졌으니 랜딩페이지를 피드홈으로 변경 (/feed/feed/groupNo)
			landing = "redirect:/feed/feed/"+groupNo; 
		}
		
		return landing;
	}
	
	//게시물 수정 뷰로 기존 post정보 모델 태워 보내기 
	@GetMapping("/feed/updatePost/{postNo}")
	public String updatePostView(@PathVariable("postNo") long postNo, Model model) {
	    model.addAttribute("originalPost", ps.findByPostNo(postNo));
	    return "/feed/postUpdate";
	}
	
	//게시물 수정
	@PostMapping("/feed/updatePost")
	public String updatePost(Long postNo, String postContent) {
		Post p = ps.findByPostNo(postNo);
		p.setPostContent(postContent); //수정한 포스트내용 설정
		ps.updatePost(p);
		return "redirect:/feed/post/"+p.getFeedNo(); 
	}

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
	
	
	
	
	//------이하 댓글 관련 ----
	//댓글 등록
	@PostMapping("/feed/commentWrite")
	public String commentWrite(Comment comment) {
		long feedNo = ps.findByPostNo(comment.getPostNo()).getFeedNo();
		cs.saveComment(comment);
		return "redirect:/feed/post/"+feedNo;
	}
	
	//댓글 삭제 :ajax로 응답 보냄!
	@PostMapping("/feed/deleteComment")
	@ResponseBody
	public ResponseEntity<Void> deleteComment(long comNo) {
	    try {
	        cs.deleteComment(comNo);
	        return ResponseEntity.ok().build(); // 성공 응답
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 에러 응답
	    }
	}
	
}
