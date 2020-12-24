package com.posts.api.posts

import com.posts.api.error.DataNotFoundException
import com.posts.api.error.ServiceException
import com.posts.api.follows.FollowId
import com.posts.api.follows.FollowRepo
import com.posts.api.users.User
import com.posts.api.votes.VoteId
import com.posts.api.votes.VoteRepo
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PostService(
  private val postRepo: PostRepo,
  private val voteRepo: VoteRepo,
  private val followRepo: FollowRepo,
) {
  @Transactional
  fun findAll(createdat: String, search: String, authUser: Any?, limit: Int): List<Post> {
    val posts = getPosts(getDateFromCursor(createdat), search, PageRequest.of(0, limit))
    if (authUser == null) return posts
    return getPostsForUser((authUser as User).id, posts)
  }

  @Transactional
  fun findOne(id: Long, authUser: Any?): Post {
    val post = getPostById(id)
    if (authUser == null) return post
    return getPostForUser((authUser as User).id, post)
  }

  fun create(post: Post): Post = postRepo.save(post)

  fun update(id: Long, updatedPost: Post): Post {
    val post = getPostById(id).apply {
      title = updatedPost.title
      text = updatedPost.text
      updatedat = LocalDateTime.now()
    }
    if (updatedPost.user.id != post.user.id) throw ServiceException("Needs same user as creator of post to update.")
    return postRepo.save(post)
  }

  fun delete(id: Long, user: User) {
    val post = getPostById(id)
    if (post.user.id != user.id) throw ServiceException("Needs same user as creator of post to delete.")
    postRepo.deleteById(post.id)
  }

  private fun getPosts(
    cursorDate: LocalDateTime,
    search: String,
    limit: PageRequest,
  ): List<Post> {
    if (search.isBlank()) return postRepo.findPosts(cursorDate, limit)
    return postRepo.findPostsSearch(cursorDate, search, limit)
  }

  private fun getDateFromCursor(createdat: String) =
    if (createdat.isBlank()) LocalDateTime.now() else LocalDateTime.parse(createdat)

  private fun getPostsForUser(
    userId: Long,
    posts: List<Post>,
  ): List<Post> {
    val followees = getFollowees(userId)
    val postIdToValue = getMapOfUserPostsVotes(userId)
    return posts.map { post ->
      if (post.user.id in followees) post.isFollow = true
      if (post.id in postIdToValue) post.voteValue = postIdToValue.getValue(post.id)
      post
    }
  }

  private fun getFollowees(userId: Long) =
    followRepo.findAllByFromId(userId).map { it.to.id }.toSet()

  private fun getMapOfUserPostsVotes(userId: Long): Map<Long, Int> {
    val postIdToValue = hashMapOf<Long, Int>()
    for (userVote in voteRepo.findAllByUserId(userId)) {
      postIdToValue[userVote.post.id] = userVote.value
    }
    return postIdToValue
  }

  private fun getPostById(id: Long): Post = postRepo.findByIdOrNull(id)
    ?: throw DataNotFoundException("Post not found.")

  private fun getPostForUser(userId: Long, post: Post): Post {
    post.isFollow = followRepo.existsById(FollowId(userId, post.user.id))
    val userVote = voteRepo.findByIdOrNull(VoteId(userId, post.id))
    if (userVote != null) post.voteValue = userVote.value
    return post
  }
}
