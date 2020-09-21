package com.postit.api.conf

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@Configuration
class PostitConfiguration {

  @Bean
  fun localValidatorFactoryBean(): LocalValidatorFactoryBean? = LocalValidatorFactoryBean()
}