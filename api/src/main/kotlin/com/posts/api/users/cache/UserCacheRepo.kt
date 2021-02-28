package com.posts.api.users.cache

import org.springframework.data.repository.CrudRepository

internal interface UserCacheRepo : CrudRepository<UserCache, String>