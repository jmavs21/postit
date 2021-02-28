package com.posts.api.follows

import com.posts.api.users.User

const val FOLLOWED = "Followed"
const val UNFOLLOWED = "Unfollowed"

interface FollowService {

  fun findAllByFromId(userId: Long): List<Follow>

  fun existsById(followId: FollowId): Boolean

  fun create(from: User, to: User): String

  fun findFollows(fromId: Long): List<Follow>

  fun findFollowers(toId: Long): List<Follow>
}