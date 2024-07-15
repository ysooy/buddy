package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	// 정적 리소스에 대한 핸들러 등록하기
        registry.addResourceHandler("/images/**")
        // /images/** 로 시작하는 경우 해당 요청을 정적 리소스로 처리하도록 핸들러 추가
                .addResourceLocations("classpath:/static/images/");
        		// classpath:/static/images/에 설정된 폴더를 기준으로 파일 찾아올것.
    }
}
