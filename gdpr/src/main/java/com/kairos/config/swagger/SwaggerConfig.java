package com.kairos.config.swagger;


import com.google.common.base.Predicate;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Predicates.or;
import static com.kairos.constants.ApiConstant.API_V1;
import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@Configuration
@EnableAutoConfiguration
@Profile({"local","development","qa","production"})
public class SwaggerConfig {

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("kairos-gdpr-api")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.kairos.controller"))
                .paths(postManagePaths())
                .build()
                .globalOperationParameters(additionalParametersCriteria());
    }

    private List<Parameter> additionalParametersCriteria(){
        ParameterBuilder paramsBuilder = new ParameterBuilder();
        List<Parameter> parametersList = new ArrayList<>();
        parametersList.add(
                paramsBuilder
                        .name("organizationId")
                        .modelRef(new ModelRef("long"))
                        .parameterType("path")
                        .required(true)
                        .build());
        parametersList.add(
                paramsBuilder
                        .name("Authorization")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(true)
                        .build());
        return parametersList;
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
