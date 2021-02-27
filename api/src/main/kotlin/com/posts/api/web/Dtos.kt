package com.posts.api.web

import com.posts.api.follows.Follow
import com.posts.api.posts.Post
import com.posts.api.users.User
import java.time.LocalDateTime
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

const val TEXT_SNIPPET_SIZE = 200

data class AuthDtoReq(
  @field:Size(min = 1, max = 50)
  val email: String,

  @field:Size(min = 1, max = 50)
  val password: String,
)

data class UserCreateDtoReq(
  @field:Size(min = 1, max = 50)
  @field:Pattern(regexp = "^[A-Za-z0-9]*\$")
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
  @field:Pattern(regexp = "^[A-Za-z0-9]*\$")
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
  var createdate: LocalDateTime?,
  var updatedate: LocalDateTime?,
  var id: Long?,
)

fun User.toDto() = UserDto(name, email, createdate, updatedate, id)

data class PostFeedDto(
  var posts: Iterable<PostDto>,
  var hasMore: Boolean,
)

data class PostDto(
  var title: String,
  var text: String,
  var points: Int,
  var voteValue: Int,
  var isFollow: Boolean,
  var user: UserDto,
  var createdate: LocalDateTime?,
  var updatedate: LocalDateTime?,
  var id: Long?,
)

fun Post.toDto() =
  PostDto(title, text, points, voteValue, isFollow, user.toDto(), createdate, updatedate, id)

fun Post.toSnippetDto() = PostDto(
  title,
  getTextSnippet(text),
  points,
  voteValue,
  isFollow,
  user.toDto(),
  createdate,
  updatedate,
  id
)

data class VoteCreateDtoReq(
  var postId: Long,
  var isUpVote: Boolean,
)

data class FollowCreateDtoReq(
  var toId: Long,
)

fun Follow.toDto() = to.toDto()

fun Follow.toDtoFrom() = from.toDto()

private fun getTextSnippet(text: String): String {
  if (text.length <= TEXT_SNIPPET_SIZE) return text
  return "${text.substring(0, TEXT_SNIPPET_SIZE)}..."
}