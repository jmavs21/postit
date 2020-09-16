package com.postit.postitserver.service

import com.postit.postitserver.error.ErrorFieldException
import com.postit.postitserver.model.Post
import com.postit.postitserver.model.User
import com.postit.postitserver.repo.PostRepo
import com.postit.postitserver.repo.UserRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepo: UserRepo,
    private val passwordEncoder: PasswordEncoder) {

  fun findAll(): Iterable<User> = userRepo.findAll()

  fun findOne(id: Long): User = getUserById(id)

  fun create(user: User): User {
    if (userRepo.findOneByEmail(user.email) != null) throw ErrorFieldException(hashMapOf("email" to "the email already exists"), HttpStatus.BAD_REQUEST)
    user.password = passwordEncoder.encode(user.password)
    return userRepo.save(user)
  }

  fun update(id: Long, updatedUser: User): User {
    val user = getUserById(id)
    user.name = updatedUser.name
    user.password = passwordEncoder.encode(updatedUser.password)
    user.updatedat = LocalDateTime.now()
    return userRepo.save(user)
  }

  fun delete(id: Long) = userRepo.deleteById(getUserById(id).id)

  fun getUserByEmail(email: String): User = userRepo.findOneByEmail(email)
      ?: throw ErrorFieldException(hashMapOf("email" to "the email doesn't exists"), HttpStatus.BAD_REQUEST)

  private fun getUserById(id: Long): User = userRepo.findByIdOrNull(id)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User with id not found.")
}

@Service
class PostService(private val postRepo: PostRepo) {
  fun findAll(): Iterable<Post> = postRepo.findAll()

  fun findOne(id: Long): Post = getPostById(id)

  fun create(post: Post): Post = postRepo.save(post)

  fun update(id: Long, updatedPost: Post): Post {
    val post = getPostById(id)
    post.title = updatedPost.title
    post.updatedat = LocalDateTime.now()
    return postRepo.save(post)
  }

  fun delete(id: Long) = postRepo.deleteById(getPostById(id).id)

  private fun getPostById(id: Long): Post = postRepo.findByIdOrNull(id)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found.")
}