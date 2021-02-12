package com.posts.api.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableRedisRepositories
class RedisConfig(
  @Value("\${spring.redis.host}") val host: String,
  @Value("\${spring.redis.port}") val port: Int,
) {

  @Bean
  fun redisConnectionFactory(): LettuceConnectionFactory = LettuceConnectionFactory(host, port)

  @Bean
  fun redisTemplate(redisConnectionFactory: LettuceConnectionFactory): RedisTemplate<*, *> {
    val template = RedisTemplate<ByteArray, ByteArray>()
    template.setConnectionFactory(redisConnectionFactory)
    return template
  }
}