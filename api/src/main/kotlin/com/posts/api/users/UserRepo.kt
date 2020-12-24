package com.posts.api.users

import org.springframework.data.repository.CrudRepository

interface UserRepo : CrudRepository<User, Long> {

  fun findOneByEmail(email: String): User?
}