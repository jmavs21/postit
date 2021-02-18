package com.posts.api.users.cache

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("UserCache")
data class UserCache(
  @Id var email: String,
  var name: String,
  var id: Long,
)