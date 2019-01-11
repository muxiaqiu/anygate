package com.intime.soa.anygate.config;

import com.intime.soa.anygate.constants.SwaggerContants;
import com.intime.soa.framework.config.Swagger2FrameworkConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author xudb
 * @version 创建时间：2018/8/9
 * 配置可参考: https://github.com/springfox/springfox/blob/master/docs/transitioning-to-v2.md
 */
@Configuration
@EnableSwagger2
@EnableWebMvc
@ComponentScan(basePackages = {"com.intime.soa.anygate"})
public class Swagger2 extends Swagger2FrameworkConfig {

    Swagger2() {
        // 重设 system property 自定义 project 设置
        System.setProperty(com.intime.soa.framework.constants.SwaggerContants.PROPERTY_KEY_SWAGGER_PROJECT_GROUP_NAME, "AnyGateAll");
        System.setProperty(com.intime.soa.framework.constants.SwaggerContants.PROPERTY_KEY_SWAGGER_PROJECT_TITLE, "AnyGate(任意门)全部API");
        System.setProperty(com.intime.soa.framework.constants.SwaggerContants.PROPERTY_KEY_SWAGGER_PROJECT_DESCRIPTION, "AnyGate(任意门)全部API");
        System.setProperty(com.intime.soa.framework.constants.SwaggerContants.PROPERTY_KEY_SWAGGER_PROJECT_PATH_REGEX, "");
        // System.setProperty(com.intime.soa.framework.constants.SwaggerContants.PROPERTY_KEY_SWAGGER_PROJECT_VERSION, Version.getVersionString());
    }

    @Bean
    public Docket swaggerSpringMvcPluginAnyGate() {
        return getDefaultDocket()
                .apiInfo(
                        new ApiInfoBuilder()
                                .title(SwaggerContants.API_TITLE)
                                .description(SwaggerContants.API_DESCRIPTION)
                                .build())
                .groupName("AnyGate")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.intime.soa.anygate.controller"))
                .build();
    }
}