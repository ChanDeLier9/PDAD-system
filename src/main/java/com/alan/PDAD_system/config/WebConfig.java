package com.alan.PDAD_system.config;

import com.alan.PDAD_system.interceptors.LoginInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    public WebConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(3000))
                .setReadTimeout(Duration.ofMillis(10000))
                .build();
    }

    private final LoginInterceptor loginInterceptor;

    // 配置拦截器
    // 登录注册接口不拦截
    // 患者登录接口不拦截
    // 患者预测接口不拦截
    // 患者图像分析接口不拦截
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/patients/login",
                        "/fmri/patient/predict",
                        "/patients/{patientId}/image-analysis",
                        "/**/*.js", // 放行静态资源
                        "/**/*.css",
                        "/**/*.png",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.ico",
                        "/**/*.svg",
                        "/**/*.woff",
                        "/**/*.woff2",
                        "/**/*.ttf"
                )
                .excludePathPatterns("/error"); // 放行错误页面
    }

}
