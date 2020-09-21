package com.postit.api.web

import com.postit.api.model.Post
import com.postit.api.model.User
import java.time.LocalDateTime
import javax.validation.constraints.Size

data class AuthDtoReq(
    @field:Size(min = 1, max = 50) val email: String,
    @field:Size(min = 1, max = 50) val password: String)

data class UserCreateDtoReq(
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
    @field:Size(min = 1, max = 50) val name: String) {
  fun toEntity(): User {
    val user = User()
    user.name = name
    return user
  }
}

data class PostDtoReq(
    @field:Size(min = 1, max = 50) val title: String,
    @field:Size(min = 1, max = 255) val text: String) {
  fun toEntity(user: User): Post {
    return Post(title, text, user)
  }
}

data class UserDto(
    var name: String,
    var email: String,
    var createdat: LocalDateTime?,
    var updatedat: LocalDateTime?,
    var id: Long?)

fun User.toDto() = UserDto(name, email, createdat, updatedat, id)

data class PostSnippetDto(
    var title: String,
    var textSnippet: String,
    var points: Int,
    var voteValue: Int,
    var user: UserDto,
    var createdat: LocalDateTime?,
    var updatedat: LocalDateTime?,
    var id: Long
)

data class PostsDto(
    var posts: Iterable<PostSnippetDto>,
    var hasMore: Boolean
)

data class PostDto(
    var title: String,
    var text: String,
    var points: Int,
    var user: UserDto,
    var createdat: LocalDateTime?,
    var updatedat: LocalDateTime?,
    var id: Long?)

fun Post.toDto() = PostDto(title, text, points, user.toDto(), createdat, updatedat, id)
fun Post.toSnippetDto() = PostSnippetDto(title, text.substring(0, 200.coerceAtMost(text.length)), points, 0, user.toDto(), createdat, updatedat, id)

data class VoteCreateDtoReq(
    var postId: Long,
    var isUpVote: Boolean
)
