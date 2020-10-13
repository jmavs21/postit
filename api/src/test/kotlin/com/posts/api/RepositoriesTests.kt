package com.posts.api

import com.posts.api.model.UserPostVote
import com.posts.api.model.Vote
import com.posts.api.repo.PostRepo
import com.posts.api.repo.UserRepo
import com.posts.api.repo.VoteRepo
import com.posts.api.web.POSTS_LIMIT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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
    if (user == null || post == null) return
    post.points = post.points + 1
    postRepo.save(post)
    voteRepo.save(Vote(1, user, post, UserPostVote(user.id, post.id)))
    val votes = voteRepo.findAllByUserId(1)
    assertThat(votes.size).isEqualTo(1)
    assertThat(post.points).isEqualTo(63)
  }
}