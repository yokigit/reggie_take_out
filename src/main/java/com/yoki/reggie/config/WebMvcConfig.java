package com.yoki.reggie.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.yoki.reggie.common.JacksonObjectMapper;
import com.yoki.reggie.interceptor.LoginCheckInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-11 11:09
 */
@Slf4j
@Configuration
@EnableSwagger2    //开启swagger文档功能
@EnableKnife4j     //
public class WebMvcConfig extends WebMvcConfigurationSupport {
//    @Resource
//    private LoginCheckInterceptor loginCheckInterceptor;

    /**
     * 静态资源默认放在 static目录下，放在其他目录不能访问
     * 设置静态资源映射 (路径 /backend/** 对资源 classpath:/backend/ 的映射)
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开启静态资源映射...");
        registry.addResourceHandler("backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("front/**").addResourceLocations("classpath:/front/");

        //
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


    /**
     * 扩展mvc框架的消息转换器
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将java对象转换json
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        //将上面的消息转换器追加到mvc框架的转换器集合中
        converters.add(0, messageConverter);  //index:0，优先使用自定义的消息转换器
    }

    /**
     * 登录权限拦截器
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
//        //不拦截的路径
//        List<String> excludePathPatterns = new ArrayList<>();
//        excludePathPatterns.add("/employee/login");
//        excludePathPatterns.add("/employee/logout");
//        excludePathPatterns.add("/employee/page");
//        excludePathPatterns.add("/backend/**");
//        excludePathPatterns.add("/front/**");
//
//        registry.addInterceptor(loginCheckInterceptor).addPathPatterns("/**").excludePathPatterns(excludePathPatterns);
    }

    /**
     * 生成接口文档
     * @return
     */
    @Bean
    public Docket createRestApi() {
        // 文档类型
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.yoki.reggie.controller"))   //需要扫描controller包才能生成接口
                .paths(PathSelectors.any())
                .build();
    }

    //描述接口文档
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("瑞吉外卖")
                .version("1.0")
                .description("瑞吉外卖接口文档")
                .build();
    }
}
