package com.posts.api.posts

import com.posts.api.users.User

interface PostService {

  fun findAll(createdate: String, search: String, authUser: Any?, limit: Int): List<Post>

  fun findOne(id: Long, authUser: Any?): Post

  fun create(post: Post): Post

  fun update(id: Long, updatedPost: Post): Post

  fun delete(id: Long, user: User)
}
