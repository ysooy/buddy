package com.example.demo.vo;

import lombok.Data;

@Data
public class OAuthToken {
	private String access_token;
	private String token_type;
	private String refresh_token;
	private int expires_in;
	private String refresh_token_expires_in;
	private String scope;

}
