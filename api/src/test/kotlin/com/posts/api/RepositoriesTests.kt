package com.posts.api

import com.posts.api.model.Follow
import com.posts.api.model.FollowId
import com.posts.api.model.VoteId
import com.posts.api.model.Vote
import com.posts.api.repo.FollowRepo
import com.posts.api.repo.PostRepo
import com.posts.api.repo.UserRepo
import com.posts.api.repo.VoteRepo
import com.posts.api.web.POSTS_LIMIT
import org.assertj.core.api.Assertions.assertThat
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

  @Test
  fun `when userRepo-findOneByEmail then user`() {
    val user = userRepo.findOneByEmail("bob@bob.com")
    assertThat(user?.name).isEqualTo("Bob")
  }

  @Test
  fun `when postRepo-findPostsFeed then post feed with limit`() {
    val postsFeed = postRepo.findPostsFeed(LocalDateTime.now(), PageRequest.of(0, POSTS_LIMIT))
    assertThat(postsFeed.size).isEqualTo(POSTS_LIMIT)
  }

  @Test
  fun `when postRepo-findPostsFeedSearch with word 'down' on title or text then post feed with limit`() {
    val postsFeedSearch =
      postRepo.findPostsFeedSearch(LocalDateTime.now(), "down", PageRequest.of(0, POSTS_LIMIT))
    assertThat(postsFeedSearch.size).isEqualTo(1)
  }

  @Test
  fun `when voteRepo-findAllByUserId with user up vote post then votes`() {
    val user = userRepo.findByIdOrNull(1)
    val post = postRepo.findByIdOrNull(1)
    if (user == null || post == null) fail("users where not found.")
    post.points = post.points + 1
    postRepo.save(post)
    voteRepo.save(Vote(1, user, post, VoteId(user.id, post.id)))
    val votes = voteRepo.findAllByUserId(1)
    assertThat(votes.size).isEqualTo(1)
    assertThat(post.points).isEqualTo(63)
  }

  @Test
  fun `when followRepo-findAllByFromId with user then follows`() {
    val from = userRepo.findByIdOrNull(1)
    val to = userRepo.findByIdOrNull(2)
    if (from == null || to == null) fail("users where not found.")
    followRepo.save(Follow(from, to, FollowId(from.id, to.id)))
    val follow = followRepo.findAllByFromId(1).firstOrNull { it.to.id == to.id }
    assertThat(follow?.to?.id).isEqualTo(to.id)
  }
}