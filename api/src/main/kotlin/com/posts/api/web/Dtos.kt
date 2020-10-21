package com.posts.api.web

import com.posts.api.model.Post
import com.posts.api.model.User
import java.time.LocalDateTime
import javax.validation.constraints.Size

data class AuthDtoReq(
  @field:Size(min = 1, max = 50)
  val email: String,

  @field:Size(min = 1, max = 50)
  val password: String,
)

data class UserCreateDtoReq(
  @field:Size(min = 1, max = 50)
  val name: String,

  @field:Size(min = 1, max = 50)
  val email: String,

  @field:Size(min = 1, max = 50)
  val password: String,
) {
  fun toEntity(): User {
    return User().apply {
      name = this@UserCreateDtoReq.name
      password = this@UserCreateDtoReq.password
      email = this@UserCreateDtoReq.email
    }
  }
}

data class UserUpdateDtoReq(
  @field:Size(min = 1, max = 50)
  val name: String,
) {
  fun toEntity(): User {
    return User().apply { name = this@UserUpdateDtoReq.name }
  }
}

data class PostDtoReq(
  @field:Size(min = 1, max = 50)
  val title: String,

  @field:Size(min = 1, max = 500)
  val text: String,
) {
  fun toEntity(user: User): Post {
    return Post(title, text, user)
  }
}

data class UserDto(
  var name: String,
  var email: String,
  var createdat: LocalDateTime?,
  var updatedat: LocalDateTime?,
  var id: Long?,
)

fun User.toDto() = UserDto(name, email, createdat, updatedat, id)

data class PostSnippetDto(
  var title: String,
  var textSnippet: String,
  var points: Int,
  var voteValue: Int,
  var isFollow: Boolean,
  var user: UserDto,
  var createdat: LocalDateTime?,
  var updatedat: LocalDateTime?,
  var id: Long,
)

data class PostsDto(
  var posts: Iterable<PostSnippetDto>,
  var hasMore: Boolean,
)

data class PostDto(
  var title: String,
  var text: String,
  var points: Int,
  var user: UserDto,
  var createdat: LocalDateTime?,
  var updatedat: LocalDateTime?,
  var id: Long?,
)

fun Post.toDto() = PostDto(title, text, points, user.toDto(), createdat, updatedat, id)

fun Post.toSnippetDto() = PostSnippetDto(
  title,
  text.substring(0, minOf(text.length, 200)),
  points,
  0,
  false,
  user.toDto(),
  createdat,
  updatedat,
  id
)

data class VoteCreateDtoReq(
  var postId: Long,
  var isUpVote: Boolean,
)

data class FollowCreateDtoReq(
  var toId: Long,
)
