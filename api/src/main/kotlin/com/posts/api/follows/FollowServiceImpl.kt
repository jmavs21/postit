package com.posts.api.follows

import com.posts.api.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class FollowServiceImpl(private val followRepo: FollowRepo) : FollowService {

  override fun findAllByFromId(userId: Long): List<Follow> = followRepo.findAllByFromId(userId)

  override fun existsById(followId: FollowId): Boolean = followRepo.existsById(followId)

  @Transactional
  override fun create(from: User, to: User): String {
    if (from.id == to.id) return "Unchanged"
    val follow = followRepo.findAllByFromId(from.id).firstOrNull { it.to.id == to.id }
    return if (follow == null) {
      followRepo.save(Follow(from, to, FollowId(from.id, to.id)))
      FOLLOWED
    } else {
      followRepo.delete(follow)
      UNFOLLOWED
    }
  }

  override fun findFollows(fromId: Long): List<Follow> = followRepo.findAllByFromId(fromId)

  override fun findFollowers(toId: Long): List<Follow> = followRepo.findAllByToId(toId)
}