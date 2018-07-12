package com.kairos.config.swagger;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static com.kairos.constants.ApiConstants.API_V1;
import static springfox.documentation.builders.PathSelectors.regex;

/**
    Configuration for Swagger Framework
    1.Controllers to include
    2. API_V1 information
    3.Enable Swagger Framework
 **/

@EnableSwagger2
@ComponentScan("com.kairos.controller")
@Configuration
@Profile({"local","development","release","production"})
public class SwaggerConfig {

    /**
     * List all scanned controllers with @Api annotation and group them.
     * you can define your group here.
     * @return Docket
     */
    @Bean
    public Docket postApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("kairos-api")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.kairos.controller"))
                .paths(postManagePaths())
                .build();
    }

   /**
   * URL to scan to create documentation,
   * you can set regex to include  matching controller to be documented.
   */
    private Predicate<String> postManagePaths() {
        return or(
                regex(API_V1 +".*")
        );
    }





    /*
    * Stores information relation to API_V1:
    * 1. Title
    * 2. Description
    * 3. Terms & Conditions
    * 4. license
    * 5. licenseURL
    * 6. version
    */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Vcore API_V1")
                .description("API_V1 to be access by client")
                .termsOfServiceUrl("http://springfox.io")
                .contact("springfox")
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                .version("2.0")
                .build();
    }


}
