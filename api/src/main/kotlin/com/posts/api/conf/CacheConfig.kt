package com.posts.api.conf

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.stereotype.Component
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

private var redisServer: RedisServer? = null

@Configuration
@EnableRedisRepositories
class RedisConfig(
  @Value("\${spring.redis.host}") val host: String,
  @Value("\${spring.redis.port}") val port: Int,
) {

  @Bean
  fun redisConnectionFactory(): LettuceConnectionFactory = LettuceConnectionFactory(host, port)

  @Bean
  fun redisTemplate(redisConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, Any> {
    return RedisTemplate<String, Any>().apply {
      setConnectionFactory(redisConnectionFactory)
      valueSerializer = GenericToStringSerializer(Any::class.java)
    }
  }
}

@Component
class EmbeddedRedis(@Value("\${spring.redis.port}") val port: Int) {

  @PostConstruct
  fun postConstruct() {
    if (redisServer == null || !redisServer!!.isActive) {
      redisServer = RedisServer(port)
      redisServer!!.start()
    }
  }

  @PreDestroy
  fun preDestroy() {
    if (redisServer != null) redisServer!!.stop()
  }
}