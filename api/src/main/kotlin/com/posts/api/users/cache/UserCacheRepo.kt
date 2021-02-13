package com.posts.api.users.cache

import org.springframework.data.repository.CrudRepository

interface UserCacheRepo : CrudRepository<UserCache, String>