package com.posts.api.service

import com.posts.api.error.ErrorFieldException
import com.posts.api.model.*
import com.posts.api.repo.FollowRepo
import com.posts.api.repo.PostRepo
import com.posts.api.repo.UserRepo
import com.posts.api.repo.VoteRepo
import com.posts.api.web.PostDto
import com.posts.api.web.PostSnippetDto
import com.posts.api.web.toDto
import com.posts.api.web.toSnippetDto
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class UserService(
  private val userRepo: UserRepo,
  private val passwordEncoder: PasswordEncoder,
) {
  fun findAll(): Iterable<User> = userRepo.findAll()

  fun findOne(id: Long): User = getUserById(id)

  fun create(user: User): User {
    if (userRepo.findOneByEmail(user.email) != null) throw ErrorFieldException(hashMapOf("email" to "the email already exists"),
      HttpStatus.BAD_REQUEST)
    user.password = passwordEncoder.encode(user.password)
    return userRepo.save(user)
  }

  fun update(id: Long, updatedUser: User): User {
    val user = getUserById(id).apply {
      name = updatedUser.name
      updatedat = LocalDateTime.now()
    }
    return userRepo.save(user)
  }

  fun delete(id: Long) = userRepo.deleteById(getUserById(id).id)

  fun getUserByEmail(email: String): User = userRepo.findOneByEmail(email)
    ?: throw ErrorFieldException(
      hashMapOf("email" to "the email doesn't exists"),
      HttpStatus.BAD_REQUEST
    )

  private fun getUserById(id: Long): User = userRepo.findByIdOrNull(id)
    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User with id not found.")
}

@Service
class PostService(
  private val postRepo: PostRepo,
  private val voteRepo: VoteRepo,
  private val followRepo: FollowRepo,
) {
  @Transactional
  fun findAll(createdat: String, search: String, authUser: Any?, limit: Int): List<PostSnippetDto> {
    val postsFeed = getPostsFeed(getDateFromCursor(createdat), search, PageRequest.of(0, limit))
    if (authUser == null) return postsFeed.toList().map { it.toSnippetDto() }
    return getPostsFeedForUser((authUser as User).id, postsFeed)
  }

  @Transactional
  fun findOne(id: Long, authUser: Any?): PostDto {
    val post = getPostById(id)
    if (authUser == null) return post.toDto()
    return getPostForUser((authUser as User).id, post)
  }

  fun create(post: Post): Post = postRepo.save(post)

  fun update(id: Long, updatedPost: Post): Post {
    val post = getPostById(id).apply {
      title = updatedPost.title
      text = updatedPost.text
      updatedat = LocalDateTime.now()
    }
    if (updatedPost.user.id != post.user.id) throw ResponseStatusException(
      HttpStatus.BAD_REQUEST,
      "Needs same user as creator of post to update."
    )
    return postRepo.save(post)
  }

  fun delete(id: Long, user: User) {
    val post = getPostById(id)
    if (post.user.id != user.id) throw ResponseStatusException(
      HttpStatus.BAD_REQUEST,
      "Needs same user as creator of post to delete."
    )
    postRepo.deleteById(post.id)
  }

  private fun getPostsFeed(
    cursorDate: LocalDateTime,
    search: String,
    limit: PageRequest,
  ): List<Post> =
    if (search.isBlank()) postRepo.findPostsFeed(
      cursorDate,
      limit
    ) else postRepo.findPostsFeedSearch(
      cursorDate,
      search,
      limit
    )

  private fun getDateFromCursor(createdat: String) =
    if (createdat.isBlank()) LocalDateTime.now() else LocalDateTime.parse(createdat)

  private fun getPostsFeedForUser(
    userId: Long,
    postsFeed: List<Post>,
  ): List<PostSnippetDto> {
    val followees = getFollowees(userId)
    val postIdToValue = getMapOfUserPostsVotes(userId)
    return postsFeed.map { post ->
      val postDto = post.toSnippetDto()
      if (post.user.id in followees) postDto.isFollow = true
      if (postDto.id in postIdToValue) postDto.voteValue = postIdToValue.getValue(postDto.id)
      postDto
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
    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found.")

  private fun getPostForUser(userId: Long, post: Post): PostDto {
    val postDto = post.toDto()
    postDto.isFollow = followRepo.existsById(FollowId(userId, post.user.id))
    val userVote = voteRepo.findByIdOrNull(VoteId(userId, post.id))
    if (userVote != null) postDto.voteValue = userVote.value
    return postDto
  }
}

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

@Service
class FollowService(private val followRepo: FollowRepo, private val userRepo: UserRepo) {
  @Transactional
  fun create(from: User, toId: Long): String {
    val to = userRepo.findByIdOrNull(toId)
    if (to == null || from.id == to.id) return "Unchanged"
    val follow = followRepo.findAllByFromId(from.id).firstOrNull { it.to.id == to.id }
    return if (follow == null) {
      followRepo.save(Follow(from, to, FollowId(from.id, to.id)))
      "Followed"
    } else {
      followRepo.delete(follow)
      "Unfollowed"
    }
  }

  fun findFollows(fromId: Long): List<Follow> = followRepo.findAllByFromId(fromId)

  fun findFollowers(toId: Long): List<Follow> = followRepo.findAllByToId(toId)
}