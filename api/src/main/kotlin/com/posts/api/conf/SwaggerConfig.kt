package com.posts.api.conf

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.time.LocalDateTime

@Configuration
@EnableSwagger2
class SwaggerConfig {

  @Bean
  fun api(): Docket =
    Docket(DocumentationType.SWAGGER_2)
      .apiInfo(ApiInfoBuilder().title("Posts API").build())
      .select()
      .apis(RequestHandlerSelectors.basePackage("com.posts.api.web"))
      .paths(PathSelectors.any()).build()
      .directModelSubstitute(LocalDateTime::class.java, String::class.java)
      .genericModelSubstitutes(ResponseEntity::class.java)
}