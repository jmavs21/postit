package com.posts.api.users

import com.posts.api.error.DataNotFoundException
import com.posts.api.error.FieldException
import com.posts.api.users.cache.UserCache
import com.posts.api.users.cache.UserCacheRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
internal class UserServiceImpl(
  private val userRepo: UserRepo,
  private val userCacheRepo: UserCacheRepo,
) :
  UserService {

  override fun findAll(): Iterable<User> = userRepo.findAll()

  override fun findOne(id: Long): User = getUserById(id)

  @Transactional
  override fun create(user: User, encodedPassword: String): User {
    if (userRepo.findOneByEmail(user.email) != null) throw FieldException(mutableMapOf("email" to "the email already exists"))
    user.password = encodedPassword
    return userRepo.save(user)
  }

  @Transactional
  override fun update(id: Long, updatedUser: User): User {
    val user = getUserById(id).apply {
      name = updatedUser.name
      updatedate = LocalDateTime.now()
    }
    userCacheRepo.save(UserCache(user.email, user.name, user.id))
    return userRepo.save(user)
  }

  @Transactional
  override fun delete(id: Long) {
    val user = getUserById(id)
    userRepo.deleteById(user.id)
    userCacheRepo.deleteById(user.email)
  }

  override fun getUserByEmail(email: String): User {
    val userCache = userCacheRepo.findByIdOrNull(email)
    if (userCache != null) return User().apply {
      this.email = userCache.email
      this.name = userCache.name
      this.id = userCache.id
    }
    val user = userRepo.findOneByEmail(email)
      ?: throw FieldException(mutableMapOf("email" to "the email doesn't exists"))
    userCacheRepo.save(UserCache(user.email, user.name, user.id))
    return user
  }

  override fun getUserWithPassByEmail(email: String): User = userRepo.findOneByEmail(email)
    ?: throw FieldException(mutableMapOf("email" to "the email doesn't exists"))

  private fun getUserById(id: Long): User = userRepo.findByIdOrNull(id)
    ?: throw DataNotFoundException("User with id not found.")
}