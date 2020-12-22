package com.posts.api.votes

import com.posts.api.posts.PostRepo
import com.posts.api.users.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VoteService(private val voteRepo: VoteRepo, private val postRepo: PostRepo) {
  @Transactional
  fun create(isUpVote: Boolean, postId: Long, user: User): Int {
    val post = postRepo.findByIdOrNull(postId) ?: return 0
    val voteValue = if (isUpVote) 1 else -1
    val lastVote = voteRepo.findByIdOrNull(VoteId(user.id, postId))
    when {
      lastVote == null -> post.points = post.points + voteValue
      lastVote.value != voteValue -> post.points = post.points + (2 * voteValue)
      else -> return post.points
    }
    postRepo.save(post)
    voteRepo.save(Vote(voteValue, user, post, VoteId(user.id, postId)))
    return post.points
  }
}