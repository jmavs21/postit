package com.posts.api

import com.jayway.jsonpath.JsonPath
import com.posts.api.conf.X_AUTH_TOKE
import com.posts.api.web.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllersTests {

  @Autowired
  lateinit var testRestTemplate: TestRestTemplate

  @Nested
  inner class AuthControllerTests {
    @Test
    fun `when api-auth POST with valid email and password then token and 200`() {
      val response = testRestTemplate.postForEntity<String>(AUTH_API, AuthDtoReq("bob@bob.com", "bob"), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).contains(".")
    }

    @Test
    fun `when api-auth POST with invalid email then 400`() {
      val response = testRestTemplate.postForEntity<String>(AUTH_API, AuthDtoReq("bob2@bob.com", "bob"), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
      assertThat(JsonPath.parse(response.body).read<String>("$.email").equals("the email doesn't exists", true))
    }

    @Test
    fun `when api-auth POST with invalid password then 400`() {
      val response = testRestTemplate.postForEntity<String>(AUTH_API, AuthDtoReq("bob@bob.com", "bob2"), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
      assertThat(JsonPath.parse(response.body).read<String>("$.password").equals("incorrect email or password", true))
    }
  }

  @Nested
  inner class UsersControllerTests {
    @Test
    fun `when api-users GET then users and 200`() {
      val response: ResponseEntity<Array<UserDto>> = testRestTemplate.getForEntity(USERS_API, Array<UserDto>::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.size).isEqualTo(30)
    }

    @Test
    fun `when api-users-{id} GET with valid id and headers then user and 200`() {
      val response: ResponseEntity<UserDto> = testRestTemplate.exchange("$USERS_API/1", HttpMethod.GET, HttpEntity<Any>(getAuthHeaders()), UserDto::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.name).isEqualTo("Bob")
    }

    @Test
    fun `when api-users-{id} GET with valid id but no header then 401`() {
      val response: ResponseEntity<String> = testRestTemplate.getForEntity("$USERS_API/1", String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when api-users-{id} GET with invalid id but valid headers then 404`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$USERS_API/0", HttpMethod.GET, HttpEntity<Any>(getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `when api-users-me GET with valid headers then user and 200`() {
      val response: ResponseEntity<UserDto> = testRestTemplate.exchange("$USERS_API/me", HttpMethod.GET, HttpEntity<Any>(getAuthHeaders()), UserDto::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.name).contains("Bob")
    }

    @Test
    fun `when api-users-me GET with no headers then 401`() {
      val response: ResponseEntity<String> = testRestTemplate.getForEntity("$USERS_API/me", String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when api-users POST with valid new user then user token and 200`() {
      val userDtoReq = UserCreateDtoReq("Zack", "zack@mail.com", "zack")
      val response: ResponseEntity<UserDto> = testRestTemplate.postForEntity(USERS_API, userDtoReq, UserDto::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.name).contains(userDtoReq.name)
      assertThat(response.headers[X_AUTH_TOKE]?.get(0)).contains(".")
    }

    @Test
    fun `when api-users POST with invalid name, email and password for new user then 400`() {
      val response: ResponseEntity<String> = testRestTemplate.postForEntity(USERS_API, UserCreateDtoReq("", "", ""), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
      assertThat(JsonPath.parse(response.body).read<String>("$.name").equals("size must be between 1 and 50", true))
      assertThat(JsonPath.parse(response.body).read<String>("$.email").equals("size must be between 1 and 50", true))
      assertThat(JsonPath.parse(response.body).read<String>("$.password").equals("size must be between 1 and 50", true))
    }

    @Test
    fun `when api-users-{id} PUT with valid update user and headers then user token and 200`() {
      val userDtoReq = UserUpdateDtoReq("izabel")
      val response: ResponseEntity<UserDto> = testRestTemplate.exchange("$USERS_API/2", HttpMethod.PUT, HttpEntity<Any>(userDtoReq, getAuthHeaders()), UserDto::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.name).contains(userDtoReq.name)
      assertThat(response.headers[X_AUTH_TOKE]?.get(0)).contains(".")
    }

    @Test
    fun `when api-users-{id} PUT with invalid name but valid headers for update user then 400`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$USERS_API/2", HttpMethod.PUT, HttpEntity<Any>(UserUpdateDtoReq(""), getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
      assertThat(JsonPath.parse(response.body).read<String>("$.name").equals("size must be between 1 and 50", true))
    }

    @Test
    fun `when api-users-{id} PUT with valid name but invalid headers for update user then 401`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$USERS_API/2", HttpMethod.PUT, HttpEntity<Any>(UserUpdateDtoReq("izabel")), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when api-users-{id} DELETE with valid id and headers then 204`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$USERS_API/3", HttpMethod.DELETE, HttpEntity<Any>(getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `when api-users-{id} DELETE with invalid id but valid headers then 404`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$USERS_API/0", HttpMethod.DELETE, HttpEntity<Any>(getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `when api-users-{id} DELETE with valid id but no headers then 401`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$USERS_API/3", HttpMethod.DELETE, HttpEntity<Any>(HttpHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }
  }

  @Nested
  inner class PostsControllerTests {
    @Test
    fun `when api-posts GET with defaults cursor and search and valid headers then posts and 200`() {
      val response: ResponseEntity<PostsDto> = testRestTemplate.getForEntity("$POSTS_API/?cursor=&search=", PostsDto::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.posts?.count()).isEqualTo(20)
    }

    @Test
    fun `when api-posts GET with date cursor and default search and valid headers then posts and 200`() {
      val response: ResponseEntity<PostsDto> = testRestTemplate.getForEntity("$POSTS_API/?cursor=2020-06-19T20:42:44&search=", PostsDto::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.posts?.count()).isEqualTo(20)
    }

    @Test
    fun `when api-posts GET with default cursor and a search and valid headers then post and 200`() {
      val response: ResponseEntity<PostsDto> = testRestTemplate.getForEntity("$POSTS_API/?cursor=&search=down", PostsDto::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.posts?.count()).isEqualTo(1)
    }

    @Test
    fun `when api-posts GET with cursor and search and valid headers then post and 200`() {
      val response: ResponseEntity<PostsDto> = testRestTemplate.getForEntity("$POSTS_API/?cursor=2020-06-19T20:42:44&search=Distributed asymmetric structure", PostsDto::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.posts?.count()).isEqualTo(1)
    }

    @Test
    fun `when api-posts-{id} GET with valid id then post and 200`() {
      val response: ResponseEntity<PostDto> = testRestTemplate.getForEntity("$POSTS_API/1", PostDto::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.id).isEqualTo(1)
    }

    @Test
    fun `when api-posts-{id} GET with invalid id then 400`() {
      val response: ResponseEntity<String> = testRestTemplate.getForEntity("$POSTS_API/0", String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `when api-posts POST with valid new post and headers then post and 201`() {
      val postDtoReq = PostDtoReq("Some title", "Some text")
      val response: ResponseEntity<PostDto> = testRestTemplate.exchange(POSTS_API, HttpMethod.POST, HttpEntity<Any>(postDtoReq, getAuthHeaders()), PostDto::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body?.title).isEqualTo(postDtoReq.title)
    }

    @Test
    fun `when api-posts POST with valid new post but no headers then 401`() {
      val postDtoReq = PostDtoReq("Some title", "Some text")
      val response: ResponseEntity<String> = testRestTemplate.exchange(POSTS_API, HttpMethod.POST, HttpEntity<Any>(postDtoReq), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when api-posts POST with invalid new post but valid headers then 400`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange(POSTS_API, HttpMethod.POST, HttpEntity<Any>(PostDtoReq("", ""), getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
      assertThat(JsonPath.parse(response.body).read<String>("$.title").equals("size must be between 1 and 50", true))
      assertThat(JsonPath.parse(response.body).read<String>("$.text").equals("size must be between 1 and 255", true))
    }

    @Test
    fun `when api-posts PUT with valid id, update post and headers then post and 200`() {
      val postDtoReq = PostDtoReq("Updated title", "Updated text")
      val response: ResponseEntity<PostDto> = testRestTemplate.exchange("$POSTS_API/8", HttpMethod.PUT, HttpEntity(postDtoReq, getAuthHeaders()), PostDto::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.title).isEqualTo(postDtoReq.title)
    }

    @Test
    fun `when api-posts PUT with valid id, update post but invalid headers then 401`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$POSTS_API/8", HttpMethod.PUT, HttpEntity(PostDtoReq("Updated title", "Updated text")), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when api-posts PUT with valid id, headers but invalid update post then 400`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$POSTS_API/8", HttpMethod.PUT, HttpEntity(PostDtoReq("", ""), getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
      assertThat(JsonPath.parse(response.body).read<String>("$.title").equals("size must be between 1 and 50", true))
      assertThat(JsonPath.parse(response.body).read<String>("$.text").equals("size must be between 1 and 255", true))
    }

    @Test
    fun `when api-posts PUT with invalid id but valid headers and update post then 404`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$POSTS_API/0", HttpMethod.PUT, HttpEntity(PostDtoReq("Updated title", "Updated text"), getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `when api-posts PUT with valid id, update post and headers but different user in token and post then 400`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$POSTS_API/2", HttpMethod.PUT, HttpEntity(PostDtoReq("Updated title", "Updated text"), getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `when api-posts-{id} DELETE with valid id and headers then 204`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$POSTS_API/8", HttpMethod.DELETE, HttpEntity<Any>(getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `when api-posts-{id} DELETE with invalid id but valid headers then 404`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$POSTS_API/0", HttpMethod.DELETE, HttpEntity<Any>(getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `when api-posts-{id} DELETE with valid id but no headers then 401`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$POSTS_API/8", HttpMethod.DELETE, HttpEntity<Any>(HttpHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when api-posts-{id} DELETE with valid id and headers but different user in token and post then 400`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange("$POSTS_API/2", HttpMethod.PUT, HttpEntity(PostDtoReq("Updated title", "Updated text"), getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
  }

  @Nested
  inner class VotesControllerTests {
    @Test
    fun `when api-votes POST with up vote after a down vote and valid headers then vote and 201`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange(VOTES_API, HttpMethod.POST, HttpEntity(VoteCreateDtoReq(4, false), getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body).isEqualTo("47")
      val response2: ResponseEntity<String> = testRestTemplate.exchange(VOTES_API, HttpMethod.POST, HttpEntity(VoteCreateDtoReq(4, true), getAuthHeaders()), String::class.java)
      assertThat(response2.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response2.body).isEqualTo("49")
    }

    @Test
    fun `when api-votes POST with down vote after a up vote and valid headers then vote and 201`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange(VOTES_API, HttpMethod.POST, HttpEntity(VoteCreateDtoReq(3, true), getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body).isEqualTo("-41")
      val response2: ResponseEntity<String> = testRestTemplate.exchange(VOTES_API, HttpMethod.POST, HttpEntity(VoteCreateDtoReq(3, false), getAuthHeaders()), String::class.java)
      assertThat(response2.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response2.body).isEqualTo("-43")
    }

    @Test
    fun `when api-votes POST with valid vote but no headers then 401`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange(VOTES_API, HttpMethod.POST, HttpEntity(VoteCreateDtoReq(4, true)), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when api-votes POST with invalid vote but valid headers then 0 and 201`() {
      val response: ResponseEntity<String> = testRestTemplate.exchange(VOTES_API, HttpMethod.POST, HttpEntity(VoteCreateDtoReq(0, true), getAuthHeaders()), String::class.java)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body).isEqualTo("0")
    }
  }

  private fun getAuthHeaders(): HttpHeaders {
    val headers = HttpHeaders()
    headers[X_AUTH_TOKE] = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiQm9iIiwiaWQiOjEsImVtYWlsIjoiYm9iQGJvYi5jb20iLCJzdWIiOiJib2JAYm9iLmNvbSIsImlhdCI6MTYwMjI3OTE5NywiZXhwIjoxOTE3Njc5MTk3fQ.4sV4C_GE8QcedIZHgllu9s6FTq8xsJIwxqFcg5xfVHU"
    return headers
  }
}