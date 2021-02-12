package com.posts.api.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

private var redisServer: RedisServer? = null

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