package com.posts.api.follows

import com.posts.api.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

const val FOLLOWED = "Followed"
const val UNFOLLOWED = "Unfollowed"

@Service
class FollowService(private val followRepo: FollowRepo) {
  @Transactional
  fun create(from: User, to: User): String {
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

  fun findFollows(fromId: Long): List<Follow> = followRepo.findAllByFromId(fromId)

  fun findFollowers(toId: Long): List<Follow> = followRepo.findAllByToId(toId)
}