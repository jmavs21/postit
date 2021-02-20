package com.posts.api.users.cache

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("UserCache")
data class UserCache(
  @Id val email: String,
  val name: String,
  val id: Long,
)