package com.posts.api

import com.posts.api.model.Post
import com.posts.api.model.User
import com.posts.api.repo.PostRepo
import com.posts.api.repo.UserRepo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
class RepositoriesTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val userRepo: UserRepo,
    val postRepo: PostRepo
) {

  @Test
  fun `when findByIdOrNull then return post`() {
    val john = User()
    john.name = "john"
    john.email = "john@mail.com"
    john.password = "john"
    entityManager.persist(john)
    val post = Post("post 1", "text 1", john)
    entityManager.persist(post)
    entityManager.flush()
    val found = postRepo.findByIdOrNull(post.id)
    assertThat(found).isEqualTo(post)
  }

  @Test
  fun `When findOneByEmail then return User`() {
    val john = User()
    john.name = "john"
    john.email = "john@mail.com"
    john.password = "john"
    entityManager.persist(john)
    entityManager.flush()
    val user = userRepo.findOneByEmail(john.email)
    assertThat(user).isEqualTo(john)
  }
}