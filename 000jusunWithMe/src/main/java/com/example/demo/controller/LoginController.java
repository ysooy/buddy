package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.KakaoLoginService;
import com.example.demo.service.UsersService;
import com.example.demo.vo.KakaoProfile;
import com.example.demo.vo.Users;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	
	@Autowired
	private KakaoLoginService ks;
	
    @Autowired
    private UsersService us;
    
    @Value("${kakao.client_id}")
    private String kakaoClientId;
    
    //로그인화면으로 연결
    @GetMapping("login")
    public String loginPage() {
        return "/firstpage/login";
    }
    
    //카카오콜백
    @GetMapping("/auth/kakao/callback")
    public String kakaoCallback(String code, Model model, HttpSession session) {
    	
        KakaoProfile kakaoProfile = ks.kakoRequest(code);
    	
    	//기본 랜딩 페이지: 첫화면(그룹 선택 화면)
    	String page = "redirect:/firstpage/firstpage";
    	
    	
    	//id를 db에서 찾기. 없으면 회원가입 화면으로 이동
    	Users existingUser =  us.findMember(kakaoProfile.getId());
    	if(existingUser ==null) {
    		
    		//카카오 프로필 객체를 모델에 태움
        	model.addAttribute("kakaoProfile", kakaoProfile);
        	
        	//닉네임/ 프로필사진 선택동의 한 경우 예외처리의 편의성을 위해 모델 속성 따로 생성
        	if(kakaoProfile.getProperties()!=null) {
        		if(kakaoProfile.getProperties().getNickname()!=null) {
        			model.addAttribute("kakaoNickname", kakaoProfile.getProperties().getNickname());    			
        		}
        		if(kakaoProfile.getProperties().getProfile_image()!=null) {
        			model.addAttribute("kakaoProfilePhoto", kakaoProfile.getKakao_account().getProfile().getProfile_image_url());    			
        		}
        	}
    		
        	//회원가입 화면으로 이동
    		page="/firstpage/join";
    	}
    	
    	//기존 회원 로그인 시 세션에 회원정보 유지(Users 객체)
    	session.setAttribute("loginUser", existingUser);
    	return page;
    }
    
    //로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
    	session.invalidate();
    	return "redirect:/login";
    }
    
}