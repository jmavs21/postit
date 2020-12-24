package com.posts.api.web

import com.posts.api.conf.JwtTokenUtil
import com.posts.api.conf.X_AUTH_TOKEN
import com.posts.api.error.FieldException
import com.posts.api.follows.FollowId
import com.posts.api.follows.FollowService
import com.posts.api.posts.Post
import com.posts.api.posts.PostService
import com.posts.api.users.User
import com.posts.api.users.UserService
import com.posts.api.votes.VoteId
import com.posts.api.votes.VoteService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.Valid

const val AUTH_API = "/api/auth"
const val USERS_API = "/api/users"
const val POSTS_API = "/api/posts"
const val VOTES_API = "/api/votes"
const val FOLLOWS_API = "/api/follows"
const val POSTS_LIMIT = 20

@RestController
@RequestMapping(AUTH_API)
class AuthController(
  private var userService: UserService,
  private val jwtTokenUtil: JwtTokenUtil,
  private val passwordEncoder: PasswordEncoder,
) {

  @PostMapping
  fun create(@Valid @RequestBody authDtoReq: AuthDtoReq): String {
    val user = userService.getUserByEmail(authDtoReq.email)
    if (!passwordEncoder.matches(authDtoReq.password,
        user.password)
    ) throw FieldException(hashMapOf("password" to "incorrect email or password"))
    return jwtTokenUtil.generateToken(user)
  }
}

@RestController
@RequestMapping(USERS_API)
class UserController(
  private val userService: UserService,
  private val jwtTokenUtil: JwtTokenUtil,
  private val passwordEncoder: PasswordEncoder,
) {

  @GetMapping
  fun findAll(): Iterable<UserDto> = userService.findAll().map { it.toDto() }

  @GetMapping("/{id}")
  fun findOne(@PathVariable id: Long): UserDto = userService.findOne(id).toDto()

  @GetMapping("/me")
  fun findMe(auth: Authentication): UserDto = (auth.principal as User).toDto()

  @PostMapping
  @ResponseStatus(CREATED)
  fun create(@Valid @RequestBody newUserCreate: UserCreateDtoReq): ResponseEntity<UserDto> {
    val user =
      userService.create(newUserCreate.toEntity(), passwordEncoder.encode(newUserCreate.password))
    val headers = getHeadersWithToken(user)
    return ResponseEntity.created(URI("$USERS_API/${user.id}")).headers(headers).body(user.toDto())
  }

  @PutMapping("/{id}")
  fun update(
    @PathVariable id: Long,
    @Valid @RequestBody updatedUser: UserUpdateDtoReq,
  ): ResponseEntity<UserDto> {
    val user = userService.update(id, updatedUser.toEntity())
    val headers = getHeadersWithToken(user)
    return ResponseEntity.ok().headers(headers).body(user.toDto())
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  fun delete(@PathVariable id: Long) = userService.delete(id)

  private fun getHeadersWithToken(user: User): HttpHeaders {
    val headers = HttpHeaders()
    headers.set(X_AUTH_TOKEN, jwtTokenUtil.generateToken(user))
    headers.set("access-control-expose-headers", X_AUTH_TOKEN)
    return headers
  }
}

@RestController
@RequestMapping(POSTS_API)
class PostController(
  private val postService: PostService,
  private val followService: FollowService,
  private val voteService: VoteService,
) {

  @GetMapping("/")
  fun findAll(
    @RequestParam cursor: String,
    @RequestParam search: String,
    @AuthenticationPrincipal user: User?,
  ): PostFeedDto {
    var posts = postService.findAll(cursor, search, user, POSTS_LIMIT + 1)
    if (user != null) posts = addFollowsAndVotes(user.id, posts)
    return PostFeedDto(posts.toList().map { it.toSnippetDto() }.take(POSTS_LIMIT),
      posts.size == POSTS_LIMIT + 1)
  }

  @GetMapping("/{id}")
  fun findOne(@PathVariable id: Long, @AuthenticationPrincipal user: User?): PostDto {
    var post = postService.findOne(id, user)
    if (user != null) post = addFollowAndVote(user.id, post)
    return post.toDto()
  }

  @PostMapping
  @ResponseStatus(CREATED)
  fun create(
    @Valid @RequestBody newPost: PostDtoReq,
    auth: Authentication,
  ): ResponseEntity<PostDto> {
    val post = postService.create(newPost.toEntity(auth.principal as User))
    return ResponseEntity.created(URI("$POSTS_API/${post.id}")).body(post.toDto())
  }

  @PutMapping("/{id}")
  fun update(
    @PathVariable id: Long,
    @Valid @RequestBody updatedPost: PostDtoReq,
    auth: Authentication,
  ): PostDto = postService.update(id, updatedPost.toEntity(auth.principal as User)).toDto()

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  fun delete(@PathVariable id: Long, auth: Authentication) =
    postService.delete(id, auth.principal as User)

  private fun addFollowAndVote(userId: Long, post: Post): Post {
    post.isFollow = followService.existsById(FollowId(userId, post.user.id))
    val userVote = voteService.findByIdOrNull(VoteId(userId, post.id))
    if (userVote != null) post.voteValue = userVote.value
    return post
  }

  private fun addFollowsAndVotes(
    userId: Long,
    posts: List<Post>,
  ): List<Post> {
    val followees = getFollowees(userId)
    val postIdToValue = getMapOfUserPostsVotes(userId)
    return posts.map { post ->
      if (post.user.id in followees) post.isFollow = true
      if (post.id in postIdToValue) post.voteValue = postIdToValue.getValue(post.id)
      post
    }
  }

  private fun getFollowees(userId: Long) =
    followService.findAllByFromId(userId).map { it.to.id }.toSet()

  private fun getMapOfUserPostsVotes(userId: Long): Map<Long, Int> {
    val postIdToValue = hashMapOf<Long, Int>()
    for (userVote in voteService.findAllByUserId(userId)) {
      postIdToValue[userVote.post.id] = userVote.value
    }
    return postIdToValue
  }
}

@RestController
@RequestMapping(VOTES_API)
class VoteController(private val voteService: VoteService, private val postService: PostService) {

  @PostMapping
  @ResponseStatus(CREATED)
  fun create(@Valid @RequestBody newVote: VoteCreateDtoReq, auth: Authentication): Int {
    val post = postService.findOne(newVote.postId, null)
    post.points = voteService.create(newVote.isUpVote, post, auth.principal as User)
    postService.update(post.id, post)
    return post.points
  }
}

@RestController
@RequestMapping(FOLLOWS_API)
class FollowController(
  private val followService: FollowService,
  private val userService: UserService,
) {

  @PostMapping
  @ResponseStatus(CREATED)
  fun create(@Valid @RequestBody newFollow: FollowCreateDtoReq, auth: Authentication): String =
    followService.create(auth.principal as User, userService.findOne(newFollow.toId))

  @GetMapping("/{id}")
  fun findFollows(@PathVariable id: Long): List<UserDto> =
    followService.findFollows(id).map { it.toDto() }

  @GetMapping("/to/{id}")
  fun findFollowers(@PathVariable id: Long): List<UserDto> =
    followService.findFollowers(id).map { it.toDtoFrom() }
}