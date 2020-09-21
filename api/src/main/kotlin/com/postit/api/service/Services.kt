package com.postit.api.service

import com.postit.api.error.ErrorFieldException
import com.postit.api.model.Post
import com.postit.api.model.User
import com.postit.api.model.UserPostVote
import com.postit.api.model.Vote
import com.postit.api.repo.PostRepo
import com.postit.api.repo.UserRepo
import com.postit.api.repo.VoteRepo
import com.postit.api.web.PostSnippetDto
import com.postit.api.web.toSnippetDto
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
    private val passwordEncoder: PasswordEncoder) {

  fun findAll(): Iterable<User> = userRepo.findAll()

  fun findOne(id: Long): User = getUserById(id)

  fun create(user: User): User {
    if (userRepo.findOneByEmail(user.email) != null) throw ErrorFieldException(hashMapOf("email" to "the email already exists"), HttpStatus.BAD_REQUEST)
    user.password = passwordEncoder.encode(user.password)
    return userRepo.save(user)
  }

  fun update(id: Long, updatedUser: User): User {
    val user = getUserById(id)
    user.name = updatedUser.name
    user.updatedat = LocalDateTime.now()
    return userRepo.save(user)
  }

  fun delete(id: Long) = userRepo.deleteById(getUserById(id).id)

  fun getUserByEmail(email: String): User = userRepo.findOneByEmail(email)
      ?: throw ErrorFieldException(hashMapOf("email" to "the email doesn't exists"), HttpStatus.BAD_REQUEST)

  private fun getUserById(id: Long): User = userRepo.findByIdOrNull(id)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User with id not found.")
}

@Service
class PostService(private val postRepo: PostRepo, private val voteRepo: VoteRepo) {

  @Transactional
  fun findAll(createdat: String, authUser: Any?): List<PostSnippetDto> {
    val posts = postRepo.findTop11ByCreatedatLessThanOrderByCreatedatDesc(if (createdat.isBlank()) LocalDateTime.now() else LocalDateTime.parse(createdat))
    if (authUser == null) return posts.map { it.toSnippetDto() }
    val user = authUser as User
    val votes = voteRepo.findAllByUserId(user.id)
    val mapOfVotes = hashMapOf<Long, Int>()
    votes.forEach { mapOfVotes[it.post.id] = it.value }
    return posts.map { post ->
      val postDto = post.toSnippetDto()
        if (postDto.id in mapOfVotes) {
          postDto.voteValue = mapOfVotes.getValue(postDto.id)
        }
      postDto
    }
  }

  fun findOne(id: Long): Post = getPostById(id)

  fun create(post: Post): Post = postRepo.save(post)

  fun update(id: Long, updatedPost: Post): Post {
    val post = getPostById(id)
    if (updatedPost.user.id != post.user.id) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Needs same user as creator of post to udpate.")
    post.title = updatedPost.title
    post.text = updatedPost.text
    post.updatedat = LocalDateTime.now()
    return postRepo.save(post)
  }

  fun delete(id: Long, user: User) {
    val post = getPostById(id)
    if (post.user.id != user.id) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Needs same user as creator of post to delete.")
    postRepo.deleteById(post.id)
  }

  private fun getPostById(id: Long): Post = postRepo.findByIdOrNull(id)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found.")
}

@Service
class VoteService(private val voteRepo: VoteRepo, private val postRepo: PostRepo) {

  @Transactional
  fun create(isUpVote: Boolean, postId: Long, user: User): Int {
    val post = postRepo.findByIdOrNull(postId) ?: return 0
    val vote = if (isUpVote) 1 else -1
    val lastVote = voteRepo.findByIdOrNull(UserPostVote(user.id, postId))
    when {
      lastVote == null -> post.points = post.points + vote
      lastVote.value != vote -> post.points = post.points + (2 * vote)
      else -> return post.points
    }
    postRepo.save(post)
    voteRepo.save(Vote(vote, user, post, UserPostVote(user.id, postId)))
    return post.points
  }
}