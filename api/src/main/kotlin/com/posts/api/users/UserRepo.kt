package com.posts.api.users

import org.springframework.data.repository.CrudRepository

internal interface UserRepo : CrudRepository<User, Long> {

  fun findOneByEmail(email: String): User?
}