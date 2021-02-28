package com.posts.api.users

interface UserService {

  fun findAll(): Iterable<User>

  fun findOne(id: Long): User

  fun create(user: User, encodedPassword: String): User

  fun update(id: Long, updatedUser: User): User

  fun delete(id: Long)

  fun getUserByEmail(email: String): User

  fun getUserWithPassByEmail(email: String): User
}