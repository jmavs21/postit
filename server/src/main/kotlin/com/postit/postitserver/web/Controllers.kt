package com.postit.postitserver.web

import com.postit.postitserver.conf.JwtTokenUtil
import com.postit.postitserver.error.ErrorFieldException
import com.postit.postitserver.model.User
import com.postit.postitserver.service.PostService
import com.postit.postitserver.service.UserService
import com.postit.postitserver.service.VoteService
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

@RestController
@RequestMapping("/api/auth")
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
@RequestMapping("/api/users")
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
    headers.set("x-auth-token", token)
    headers.set("access-control-expose-headers", "x-auth-token")
    return headers
  }
}

@RestController
@RequestMapping("/api/posts")
class PostController(private val postService: PostService) {
  @GetMapping("/")
  fun findAll(@RequestParam cursor: String, @AuthenticationPrincipal user: User?): PostsDto {
    val posts = postService.findAll(cursor, user)
    return PostsDto(posts.take(10), posts.size == 11)
  }

  @GetMapping("/{id}")
  fun findOne(@PathVariable id: Long): PostDto = postService.findOne(id).toDto()

  @PostMapping
  @ResponseStatus(CREATED)
  fun create(@Valid @RequestBody newPost: PostCreateDtoReq, auth: Authentication): PostDto = postService.create(newPost.toEntity(auth.principal as User)).toDto()

//  @PutMapping("/{id}")
//  fun update(@PathVariable id: Long, @Valid @RequestBody updatedPost: PostDtoReq): PostDto = postService.update(id, updatedPost.toEntity()).toDto()

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  fun delete(@PathVariable id: Long) = postService.delete(id)
}

@RestController
@RequestMapping("/api/votes")
class VoteController(private val voteService: VoteService) {
  @PostMapping
  @ResponseStatus(CREATED)
  fun create(@Valid @RequestBody newVote: VoteCreateDtoReq, auth: Authentication): Int = voteService.create(newVote.isUpVote, newVote.postId, auth.principal as User)
}