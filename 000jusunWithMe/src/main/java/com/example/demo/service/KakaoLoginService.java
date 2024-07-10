package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.KakaoProfile;
import com.example.demo.entity.OAuthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KakaoLoginService {

   @Value("${kakao.client_id}")
    private String kakaoClientId;
	
	public KakaoProfile kakoRequest(String code) {
		KakaoProfile kakaoProfile = null;
		
    	//1. 카카오로부터 토큰을 받기 위한 1차 요청 보내기
    	RestTemplate rt = new RestTemplate();
    	
    	//http header 오브젝트 생성
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
    	
    	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    	params.add("grant_type", "authorization_code");
    	params.add("client_id",kakaoClientId );
    	params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback"); //도메인 생기면 이 부분은 변경! 
    	params.add("code", code);
    	
    	//httpheader랑 httpBody를 한 오브젝트에 담기
    	HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
    			new HttpEntity<>(params, headers);
 
    	//http 요청하기(POST). 그리고 요청 받기
    	ResponseEntity<String> response = rt.exchange(
    			"https://kauth.kakao.com/oauth/token",
    			HttpMethod.POST,
    			kakaoTokenRequest,
    			String.class
    			);
    	
    	ObjectMapper obMapper = new ObjectMapper();
    	OAuthToken oauthToken = null;
    	try {
    		//정상적으로 응답이 왔다면 oauthToken에 토큰을 저장
			oauthToken = obMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	
    	//2. 카카오로부터 회원 정보를 받기 위한 두 번째 요청 보내기
    	RestTemplate rt2 = new RestTemplate();
    	
    	//http header 오브젝트 생성
    	HttpHeaders headers2 = new HttpHeaders();
    	headers2.add("Authorization", "Bearer "+oauthToken.getAccess_token());
    	headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    	
    	//httpheader랑 httpBody를 한 오브젝트에 담기
    	HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
    			new HttpEntity<>(headers2);
 
    	//http 요청하기(POST). 그리고 요청 받기
    	ResponseEntity<String> response2 = rt2.exchange(
    			"https://kapi.kakao.com/v2/user/me",
    			HttpMethod.POST,
    			kakaoProfileRequest,
    			String.class
    			);
    	
    	ObjectMapper obMapper2 = new ObjectMapper();
    	try {
    		//정상적으로 응답이 왔다면 kakaoProfile에 카카오 프로필정보를 저장
    		kakaoProfile = obMapper2.readValue(response2.getBody(), KakaoProfile.class);
		} catch (JsonMappingException e) {
			 e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return kakaoProfile; 
	}
}
