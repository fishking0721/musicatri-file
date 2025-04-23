package org.example.oss.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Kni4JConfiguration implements WebMvcConfigurer {

    // 处理静态资源映射，例如knif4J，避免被作为接口解析
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    // Knif4J相关配置

    /**
     * 文件接口组
     */
    @Bean
    public GroupedOpenApi fileApi() {
        return GroupedOpenApi.builder()
                .group("file-api")
                .pathsToMatch("/api/v1/file/**")  // 分组匹配规则
                .build();
    }

    @Bean  // 文档基本信息
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                        .title("file server Api文档")
                        .description("文件存储服务")
                        .version("v1.0.0")
                        .contact(new Contact().name("pineclone").email("pineclone@outlook.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }

}
