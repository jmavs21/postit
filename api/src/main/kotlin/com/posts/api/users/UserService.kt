package com.posts.api.users

import com.posts.api.error.DataNotFoundException
import com.posts.api.error.FieldException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
  private val userRepo: UserRepo,
  private val passwordEncoder: PasswordEncoder,
) {
  fun findAll(): Iterable<User> = userRepo.findAll()

  fun findOne(id: Long): User = getUserById(id)

  fun create(user: User): User {
    if (userRepo.findOneByEmail(user.email) != null) throw FieldException(hashMapOf("email" to "the email already exists"))
    user.password = passwordEncoder.encode(user.password)
    return userRepo.save(user)
  }

  fun update(id: Long, updatedUser: User): User {
    val user = getUserById(id).apply {
      name = updatedUser.name
      updatedat = LocalDateTime.now()
    }
    return userRepo.save(user)
  }

  fun delete(id: Long) = userRepo.deleteById(getUserById(id).id)

  fun getUserByEmail(email: String): User = userRepo.findOneByEmail(email)
    ?: throw FieldException(hashMapOf("email" to "the email doesn't exists"))

  private fun getUserById(id: Long): User = userRepo.findByIdOrNull(id)
    ?: throw DataNotFoundException("User with id not found.")
}