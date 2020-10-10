package com.posts.api.web

import com.posts.api.conf.JwtTokenUtil
import com.posts.api.conf.X_AUTH_TOKE
import com.posts.api.error.ErrorFieldException
import com.posts.api.model.User
import com.posts.api.service.PostService
import com.posts.api.service.UserService
import com.posts.api.service.VoteService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

const val AUTH_API: String = "/api/auth"
const val USERS_API: String = "/api/users"
const val POSTS_API: String = "/api/posts"
const val VOTES_API: String = "/api/votes"

@RestController
@RequestMapping(AUTH_API)
class AuthController(
    private var userService: UserService,
    private val jwtTokenUtil: JwtTokenUtil,
    private val passwordEncoder: PasswordEncoder) {

  @PostMapping
  fun create(@Valid @RequestBody authDtoReq: AuthDtoReq): String {
    val user = userService.getUserByEmail(authDtoReq.email)
    if (!passwordEncoder.matches(authDtoReq.password, user.password)) throw ErrorFieldException(hashMapOf("password" to "incorrect email or password"), HttpStatus.BAD_REQUEST)
    return jwtTokenUtil.generateToken(user)
  }
}

@RestController
@RequestMapping(USERS_API)
class UserController(
    private val userService: UserService,
    private val jwtTokenUtil: JwtTokenUtil) {

  @GetMapping
  fun findAll(): Iterable<UserDto> = userService.findAll().map { it.toDto() }

  @GetMapping("/{id}")
  fun findOne(@PathVariable id: Long): UserDto = userService.findOne(id).toDto()

  @GetMapping("/me")
  fun findMe(auth: Authentication): UserDto {
    val user = auth.principal as User
    return user.toDto()
  }

  @PostMapping
  @ResponseStatus(CREATED)
  fun create(@Valid @RequestBody newUserCreate: UserCreateDtoReq): ResponseEntity<UserDto> {
    val user = userService.create(newUserCreate.toEntity())
    val headers = getHeadersWithToken(user)
    return ResponseEntity.ok().headers(headers).body(user.toDto())
  }

  @PutMapping("/{id}")
  fun update(@PathVariable id: Long, @Valid @RequestBody updatedUser: UserUpdateDtoReq): ResponseEntity<UserDto> {
    val user = userService.update(id, updatedUser.toEntity())
    val headers = getHeadersWithToken(user)
    return ResponseEntity.ok().headers(headers).body(user.toDto())
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  fun delete(@PathVariable id: Long) = userService.delete(id)

  private fun getHeadersWithToken(user: User): HttpHeaders {
    val token = jwtTokenUtil.generateToken(user)
    val headers = HttpHeaders()
    headers.set(X_AUTH_TOKE, token)
    headers.set("access-control-expose-headers", X_AUTH_TOKE)
    return headers
  }
}

const val POSTS_LIMIT = 20

@RestController
@RequestMapping(POSTS_API)
class PostController(private val postService: PostService) {

  @GetMapping("/")
  fun findAll(@RequestParam cursor: String, @RequestParam search: String, @AuthenticationPrincipal user: User?): PostsDto {
    val posts = postService.findAll(cursor, search, user, POSTS_LIMIT + 1)
    return PostsDto(posts.take(POSTS_LIMIT), posts.size == POSTS_LIMIT + 1)
  }

  @GetMapping("/{id}")
  fun findOne(@PathVariable id: Long): PostDto = postService.findOne(id).toDto()

  @PostMapping
  @ResponseStatus(CREATED)
  fun create(@Valid @RequestBody newPost: PostDtoReq, auth: Authentication): PostDto = postService.create(newPost.toEntity(auth.principal as User)).toDto()

  @PutMapping("/{id}")
  fun update(@PathVariable id: Long, @Valid @RequestBody updatedPost: PostDtoReq, auth: Authentication): PostDto = postService.update(id, updatedPost.toEntity(auth.principal as User)).toDto()

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  fun delete(@PathVariable id: Long, auth: Authentication) = postService.delete(id, auth.principal as User)
}

@RestController
@RequestMapping(VOTES_API)
class VoteController(private val voteService: VoteService) {

  @PostMapping
  @ResponseStatus(CREATED)
  fun create(@Valid @RequestBody newVote: VoteCreateDtoReq, auth: Authentication): Int = voteService.create(newVote.isUpVote, newVote.postId, auth.principal as User)
}