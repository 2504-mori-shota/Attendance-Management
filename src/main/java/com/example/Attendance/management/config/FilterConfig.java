package com.example.Attendance.management.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilter() {
        FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new LoginFilter());
        //ログイン情報が必要なURL
        bean.addUrlPatterns("/attendance/*");
        bean.addUrlPatterns("/home/*");
        bean.addUrlPatterns("/request/*");
        //bean.addUrlPatterns("/myrequest/*");
        //bean.addUrlPatterns("/signup/*");
        bean.addUrlPatterns("/system/*");
        bean.addUrlPatterns("/user/*");
        //bean.addUrlPatterns("/useredit/*");
        //bean.addUrlPatterns("/attendanceedit/*");

        bean.setOrder(1);
        return bean;
    }
}