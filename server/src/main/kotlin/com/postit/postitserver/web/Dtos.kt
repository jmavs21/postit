package com.postit.postitserver.web

import com.postit.postitserver.model.Post
import com.postit.postitserver.model.User
import java.time.LocalDateTime
import javax.validation.constraints.Size

data class AuthDtoReq(
    @field:Size(min = 1, max = 50) val email: String,
    @field:Size(min = 1, max = 50) val password: String)

data class UserDtoReq(
    @field:Size(min = 1, max = 50) val name: String,
    @field:Size(min = 1, max = 50) val email: String,
    @field:Size(min = 1, max = 50) val password: String) {
  fun toEntity(): User {
    val user = User()
    user.name = name
    user.password = password
    user.email = email
    return user
  }
}

data class UserUpdateDtoReq(
    @field:Size(min = 1, max = 50) val name: String,
    @field:Size(min = 1, max = 50) val password: String) {
  fun toEntity(): User {
    val user = User()
    user.name = name
    user.password = password
    return user
  }
}

data class UserDto(
    var name: String,
    var email: String,
    var password: String,
    var createdat: LocalDateTime?,
    var updatedat: LocalDateTime?,
    var id: Long?)

fun User.toDto() = UserDto(name, email, password, createdat, updatedat, id)

data class PostDtoReq(
    @field:Size(min = 1, max = 50) val title: String) {
  fun toEntity() = Post(title)
}

data class PostDto(
    var title: String,
    var createdat: LocalDateTime?,
    var updatedat: LocalDateTime?,
    var id: Long?)

fun Post.toDto() = PostDto(title, createdat, updatedat, id)