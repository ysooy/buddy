package com.example.demo.entity;

import lombok.Data;

@Data
public class KakaoProfile {
    public long id;
    public String connected_at;
    public Properties properties;
    public KakaoAccount kakao_account;
    
    @Data
    public static class Properties {
    	public String nickname; //닉네임
    	public String profile_image; //프로필사진
    	public String thumbnail_image;

    }
    @Data
    public static class KakaoAccount {
    	public boolean profile_nickname_needs_agreement;
    	public boolean profile_image_needs_agreement;
    	public Profile profile;

    }
    @Data
    public static class Profile {
    	public String nickname;
    	public String thumbnail_image_url;
    	public String profile_image_url;
    	public boolean is_default_image;
    	public boolean is_default_nickname;

    }
}
