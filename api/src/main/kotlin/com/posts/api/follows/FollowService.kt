package com.posts.api.follows

import com.posts.api.users.User
import com.posts.api.users.UserRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

const val FOLLOWED = "Followed"
const val UNFOLLOWED = "Unfollowed"

@Service
class FollowService(private val followRepo: FollowRepo, private val userRepo: UserRepo) {
  @Transactional
  fun create(from: User, toId: Long): String {
    val to = userRepo.findByIdOrNull(toId)
    if (to == null || from.id == to.id) return "Unchanged"
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