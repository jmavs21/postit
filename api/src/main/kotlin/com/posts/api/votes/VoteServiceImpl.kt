package com.posts.api.votes

import com.posts.api.posts.Post
import com.posts.api.users.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class VoteServiceImpl(private val voteRepo: VoteRepo) : VoteService {

  override fun findAllByUserId(userId: Long): List<Vote> = voteRepo.findAllByUserId(userId)

  override fun findByIdOrNull(voteId: VoteId): Vote? = voteRepo.findByIdOrNull(voteId)

  @Transactional
  override fun create(isUpVote: Boolean, post: Post, user: User): Int {
    val voteValue = if (isUpVote) 1 else -1
    val lastVote = voteRepo.findByIdOrNull(VoteId(user.id, post.id))
    when {
      lastVote == null -> post.points = post.points + voteValue
      lastVote.value != voteValue -> post.points = post.points + (2 * voteValue)
      else -> return post.points
    }
    voteRepo.save(Vote(voteValue, user, post, VoteId(user.id, post.id)))
    return post.points
  }
}