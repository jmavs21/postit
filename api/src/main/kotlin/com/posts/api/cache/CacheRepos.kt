package com.posts.api.cache

import org.springframework.data.repository.CrudRepository

interface UserCacheRepo : CrudRepository<UserCache, String>