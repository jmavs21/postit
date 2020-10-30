package com.posts.api

import com.posts.api.model.*
import com.posts.api.repo.FollowRepo
import com.posts.api.repo.PostRepo
import com.posts.api.repo.UserRepo
import com.posts.api.repo.VoteRepo
import com.posts.api.web.POSTS_LIMIT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@DataJpaTest
class RepositoriesTests @Autowired constructor(
  val userRepo: UserRepo,
  val postRepo: PostRepo,
  val voteRepo: VoteRepo,
  val followRepo: FollowRepo,
) {

  @Nested
  inner class UserRepoTests {
    @Test
    fun `when findOneByEmail then user`() {
      val user = userRepo.findOneByEmail("bob@bob.com")
      assertThat(user?.name).isEqualTo("Bob")
    }
  }

  @Nested
  inner class PostRepoTests {
    @Test
    fun `when findPostsFeed then post feed with limit`() {
      val postsFeed = postRepo.findPostsFeed(LocalDateTime.now(), PageRequest.of(0, POSTS_LIMIT))
      assertThat(postsFeed.size).isEqualTo(POSTS_LIMIT)
    }

    @Test
    fun `when findPostsFeedSearch with word 'down' on title, text or user name then post feed with limit`() {
      val postsFeedSearch =
        postRepo.findPostsFeedSearch(LocalDateTime.now(), "down", PageRequest.of(0, POSTS_LIMIT))
      assertThat(postsFeedSearch.size).isEqualTo(1)
    }
  }

  @Nested
  inner class VoteRepoTests {
    @Test
    fun `when findAllByUserId with up vote of post by user then votes`() {
      val (user, post) = getUserAndPost(1, 1)
      upVotePost(user, post)
      val votes = voteRepo.findAllByUserId(1)
      assertThat(votes.size).isEqualTo(1)
      assertThat(post.points).isEqualTo(63)
    }

    private fun getUserAndPost(userId: Long, postId: Long): Pair<User, Post> {
      val user = userRepo.findByIdOrNull(userId)
      val post = postRepo.findByIdOrNull(postId)
      if (user == null || post == null) fail("users where not found.")
      return user to post
    }

    private fun upVotePost(user: User, post: Post) {
      post.points = post.points + 1
      postRepo.save(post)
      voteRepo.save(Vote(1, user, post, VoteId(user.id, post.id)))
    }
  }

  @Nested
  inner class FollowRepoTests {
    @Test
    fun `when findAllByFromId with user then follows`() {
      val (from, to) = getFromAndToUsers(1, 2)
      followRepo.save(Follow(from, to, FollowId(from.id, to.id)))
      val follow = followRepo.findAllByFromId(1).firstOrNull { it.to.id == to.id }
      assertThat(follow?.to?.id).isEqualTo(to.id)
    }

    @Test
    fun `when findAllByToId with user then followers`() {
      val (from, to) = getFromAndToUsers(5, 1)
      followRepo.save(Follow(from, to, FollowId(from.id, to.id)))
      val follow = followRepo.findAllByToId(1).firstOrNull { it.from.id == from.id }
      assertThat(follow?.from?.id).isEqualTo(from.id)
    }

    private fun getFromAndToUsers(fromId: Long, toId: Long): Pair<User, User> {
      val from = userRepo.findByIdOrNull(fromId)
      val to = userRepo.findByIdOrNull(toId)
      if (from == null || to == null) fail("users where not found.")
      return from to to
    }
  }
}