package com.postit.postitserver.web

import com.postit.postitserver.conf.JwtUserDetailsService
import com.postit.postitserver.model.User
import com.postit.postitserver.service.PostService
import com.postit.postitserver.service.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@RestController
@RequestMapping("/api/auth")
class AuthController(private val userDetailsService: JwtUserDetailsService) {

  @PostMapping
  fun create(@RequestBody authDtoReq: AuthDtoReq) = userDetailsService.getGeneratedToken((authDtoReq.email))
}

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
  @GetMapping
  fun findAll(): Iterable<UserDto> = userService.findAll().map { it.toDto() }

  @GetMapping("/{id}")
  fun findOne(@PathVariable id: Long): UserDto = userService.findOne(id).toDto()

  @GetMapping("/me")
  fun findMe(authentication: Authentication): UserDto {
    val user = authentication.principal
    if (user == null || user !is User) throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
    return user.toDto()
  }

  @PostMapping
  @ResponseStatus(CREATED)
  fun create(@Valid @RequestBody newUser: UserDtoReq): ResponseEntity<UserDto> {
    val user = userService.create(newUser.toEntity())
    val token = userService.getTokenFromEmail(user.email)
    val headers = HttpHeaders()
    headers.set("x-auth-token", token)
    headers.set("access-control-expose-headers", "x-auth-token")
    return ResponseEntity.ok().headers(headers).body(user.toDto())
  }

  @PutMapping("/{id}")
  fun update(@PathVariable id: Long, @Valid @RequestBody updatedUser: UserDtoReq): UserDto = userService.update(id, updatedUser.toEntity()).toDto()

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  fun delete(@PathVariable id: Long) = userService.delete(id)
}

@RestController
@RequestMapping("/api/posts")
class PostController(private val postService: PostService) {
  @GetMapping
  fun findAll(): Iterable<PostDto> = postService.findAll().map { it.toDto() }

  @GetMapping("/{id}")
  fun findOne(@PathVariable id: Long): PostDto = postService.findOne(id).toDto()

  @PostMapping
  @ResponseStatus(CREATED)
  fun create(@Valid @RequestBody newPost: PostDtoReq): PostDto = postService.create(newPost.toEntity()).toDto()

  @PutMapping("/{id}")
  fun update(@PathVariable id: Long, @Valid @RequestBody updatedPost: PostDtoReq): PostDto = postService.update(id, updatedPost.toEntity()).toDto()

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  fun delete(@PathVariable id: Long) = postService.delete(id)
}