package com.example.foodrecommend.config;


import com.example.foodrecommend.interceptor.CheckTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private CheckTokenInterceptor checkTokenInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(checkTokenInterceptor)
                .addPathPatterns("/discount/**")
                .addPathPatterns("/foodComments/**")
                .addPathPatterns("/foodSku/**")
                .addPathPatterns("/foodStatsDictionary/**")
                .addPathPatterns("/merchant/**")
                .addPathPatterns("/orders/**")
                .addPathPatterns("/report/**")
                .addPathPatterns("/user/**")
                .addPathPatterns("/orderFather/**")
                .excludePathPatterns("/user/login");
//为什么不使用下面这种方法，对全部进行拦截，然后排除登录和接口文档的地址，因为这样接口文档会出问题，可能会有其他调用
//        registry.addInterceptor(checkTokenInterceptor)
//                .addPathPatterns("/**")  // 拦截所有路径
//                .excludePathPatterns("/user/login").excludePathPatterns("/doc.html");  // 排除特定路径

    }
}
