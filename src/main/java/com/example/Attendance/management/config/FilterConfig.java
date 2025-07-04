//package com.example.Attendance.management.config;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FilterConfig {
//    @Bean
//    public FilterRegistrationBean<LoginFilter> loginFilter() {
//        FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>();
//
//        bean.setFilter(new LoginFilter());
//        //ログイン情報が必要なURL
//        bean.addUrlPatterns("/home/*");
//        bean.addUrlPatterns("/management");
//        bean.addUrlPatterns("/singUp");
//        bean.addUrlPatterns("/message");
//        bean.addUrlPatterns("/userEdit/*");
//        bean.addUrlPatterns("/inset");
//        bean.addUrlPatterns("/update");
//        bean.addUrlPatterns("/management");
//        bean.addUrlPatterns("/aoi");
//        bean.addUrlPatterns("/mori");
//        bean.addUrlPatterns("/addMessage");
//        bean.addUrlPatterns("/addComment");
//        bean.addUrlPatterns("/read" );
//
//        bean.setOrder(1);
//        return bean;
//    }
//}