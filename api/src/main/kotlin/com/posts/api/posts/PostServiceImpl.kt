package com.posts.api.posts

import com.posts.api.error.DataNotFoundException
import com.posts.api.error.ServiceException
import com.posts.api.users.User
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
internal class PostServiceImpl(private val postRepo: PostRepo) : PostService {

  override fun findAll(createdate: String, search: String, authUser: Any?, limit: Int): List<Post> {
    val cursorDate = getDateFromCursor(createdate)
    val pageable = PageRequest.of(0, limit)
    if (search.isBlank()) return postRepo.findPosts(cursorDate, pageable)
    return postRepo.findPostsSearch(cursorDate, search, pageable)
  }

  override fun findOne(id: Long, authUser: Any?): Post = getPost(id)

  override fun create(post: Post): Post = postRepo.save(post)

  @Transactional
  override fun update(id: Long, updatedPost: Post): Post {
    val post = getPost(id).apply {
      title = updatedPost.title
      text = updatedPost.text
      updatedate = LocalDateTime.now()
    }
    if (updatedPost.user.id != post.user.id) throw ServiceException("Needs same user as creator of post to update.")
    return postRepo.save(post)
  }

  @Transactional
  override fun delete(id: Long, user: User) {
    val post = getPost(id)
    if (post.user.id != user.id) throw ServiceException("Needs same user as creator of post to delete.")
    postRepo.deleteById(post.id)
  }

  private fun getDateFromCursor(createdate: String) =
    if (createdate.isBlank()) LocalDateTime.now() else LocalDateTime.parse(createdate)

  private fun getPost(id: Long): Post = postRepo.findByIdOrNull(id)
    ?: throw DataNotFoundException("Post not found.")
}
