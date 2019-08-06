package com.kairos.interceptor;

import com.kairos.config.swagger.SwaggerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Created by prabjot on 21/11/16.
 */

@Configuration
@Import({SwaggerConfig.class})
class InterceptorRegister extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ExtractOrganizationAndUnitInfoInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**","/img/**","/css/**","/js/**","/images/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/","classpath:/static/images/"
                ,"classpath:/static/css/"
                ,"classpath:/static/js/");
        registry.addResourceHandler("/**").addResourceLocations("/");
    }

}
