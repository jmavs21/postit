package com.postit.postitserver.repo

import com.postit.postitserver.model.Post
import com.postit.postitserver.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepo : CrudRepository<User, Long> {
  fun findOneByEmail(email: String): User?
}

interface PostRepo : CrudRepository<Post, Long> {
  fun findOneByTitle(title: String): Post?
}
