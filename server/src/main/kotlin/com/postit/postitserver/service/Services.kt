package com.postit.postitserver.service

import com.postit.postitserver.conf.JwtUserDetailsService
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
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsService: JwtUserDetailsService) {

  fun findAll(): Iterable<User> = userRepo.findAll()

  fun findOne(id: Long): User = userRepo.findByIdOrNull(id)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")

  fun create(user: User): User {
    if (userRepo.findOneByEmail(user.email) != null) throw ResponseStatusException(HttpStatus.NOT_FOUND, "User with email already exists.")
    user.password = passwordEncoder.encode(user.password)
    return userRepo.save(user)
  }

  fun update(id: Long, updatedUser: User): User {
    val user = userRepo.findByIdOrNull(id)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
    user.name = updatedUser.name
    user.password = passwordEncoder.encode(updatedUser.password)
    user.email = updatedUser.email
    user.updatedat = LocalDateTime.now()
    return userRepo.save(user)
  }

  fun delete(id: Long) {
    userRepo.findByIdOrNull(id)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")
    userRepo.deleteById(id)
  }

  fun getTokenFromEmail(email: String) = userDetailsService.getGeneratedToken(email)
}

@Service
class PostService(private val postRepo: PostRepo) {
  fun findAll(): Iterable<Post> = postRepo.findAll()

  fun findOne(id: Long): Post = postRepo.findByIdOrNull(id)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found.")

  fun create(post: Post): Post = postRepo.save(post)

  fun update(id: Long, updatedPost: Post): Post {
    val post = postRepo.findByIdOrNull(id)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")
    post.title = updatedPost.title
    post.updatedat = LocalDateTime.now()
    return postRepo.save(post)
  }

  fun delete(id: Long) {
    postRepo.findByIdOrNull(id)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found.")
    postRepo.deleteById(id)
  }
}