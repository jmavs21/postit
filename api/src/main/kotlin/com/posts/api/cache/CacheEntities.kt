package com.posts.api.cache

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("UserCache", timeToLive = 600)
data class UserCache(
  @Id var email: String,
  var name: String,
  var id: Long,
)