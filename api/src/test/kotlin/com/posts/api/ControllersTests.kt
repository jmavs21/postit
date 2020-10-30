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
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.util.MultiValueMap

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllersTests {

  @Autowired
  lateinit var testRestTemplate: TestRestTemplate

  @Nested
  inner class AuthControllerTests {
    @Test
    fun `when AUTH_API POST with valid email and password then token and 200`() {
      val response = doPost<String>(AUTH_API, AuthDtoReq("bob@bob.com", "bob"))
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).contains(".")
    }

    @Test
    fun `when AUTH_API POST with invalid email then 400`() {
      val response = doPost<String>(AUTH_API, AuthDtoReq("bob2@bob.com", "bob"))
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
      assertThat(validJsons(response.body, mapOf("$.email" to "the email doesn't exists"))).isTrue()
    }

    @Test
    fun `when AUTH_API POST with invalid password then 400`() {
      val response = doPost<String>(AUTH_API, AuthDtoReq("bob@bob.com", "bob2"))
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
      assertThat(validJsons(response.body,
        mapOf("$.password" to "incorrect email or password"))).isTrue()
    }
  }

  @Nested
  inner class UsersControllerTests {
    @Test
    fun `when USERS_API GET then users and 200`() {
      val response = doGet<Array<UserDto>>(USERS_API)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.size).isEqualTo(30)
    }

    @Test
    fun `when USERS_API-{id} GET with valid id and headers then user and 200`() {
      val response = doExchange<UserDto>("$USERS_API/1", HttpMethod.GET, null, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.name).isEqualTo("Bob")
    }

    @Test
    fun `when USERS_API-{id} GET with valid id but no header then 401`() {
      val response = doGet<String>("$USERS_API/1")
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when USERS_API-{id} GET with invalid id but valid headers then 404`() {
      val response = doExchange<String>("$USERS_API/0", HttpMethod.GET, null, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `when USERS_API-me GET with valid headers then user and 200`() {
      val response = doExchange<UserDto>("$USERS_API/me", HttpMethod.GET, null, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.name).contains("Bob")
    }

    @Test
    fun `when USERS_API-me GET with no headers then 401`() {
      val response = doGet<String>("$USERS_API/me")
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when USERS_API POST with valid new user then user token and 200`() {
      val userDtoReq = UserCreateDtoReq("Zack", "zack@mail.com", "zack")
      val response = doPost<UserDto>(USERS_API, userDtoReq)
      doExchange<String>("$USERS_API/${response.body?.id}", HttpMethod.DELETE, null, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.name).contains(userDtoReq.name)
      assertThat(response.headers[X_AUTH_TOKE]?.get(0)).contains(".")
    }

    @Test
    fun `when USERS_API POST with invalid name, email and password for new user then 400`() {
      val response = doPost<String>(USERS_API, UserCreateDtoReq("", "", ""))
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
      assertThat(validJsons(response.body,
        mapOf("$.name" to "size must be between 1 and 50",
          "$.email" to "size must be between 1 and 50",
          "$.password" to "size must be between 1 and 50")))
    }

    @Test
    fun `when USERS_API-{id} PUT with valid update user and headers then user token and 200`() {
      val userDtoReq = UserUpdateDtoReq("izabel")
      val response = doExchange<UserDto>("$USERS_API/2",
        HttpMethod.PUT,
        userDtoReq, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.name).contains(userDtoReq.name)
      assertThat(response.headers[X_AUTH_TOKE]?.get(0)).contains(".")
    }

    @Test
    fun `when USERS_API-{id} PUT with invalid name but valid headers for update user then 400`() {
      val response = doExchange<String>("$USERS_API/2", HttpMethod.PUT, UserUpdateDtoReq(""), true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
      assertThat(validJsons(response.body, mapOf("$.name" to "size must be between 1 and 50")))
    }

    @Test
    fun `when USERS_API-{id} PUT with valid name but invalid headers for update user then 401`() {
      val response =
        doExchange<String>("$USERS_API/2", HttpMethod.PUT, UserUpdateDtoReq("izabel"), false)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when USERS_API-{id} DELETE with valid id and headers then 204`() {
      val response = doExchange<String>("$USERS_API/3", HttpMethod.DELETE, null, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `when USERS_API-{id} DELETE with invalid id but valid headers then 404`() {
      val response = doExchange<String>("$USERS_API/0", HttpMethod.DELETE, null, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `when USERS_API-{id} DELETE with valid id but no headers then 401`() {
      val response = doExchange<String>("$USERS_API/3", HttpMethod.DELETE, null, false)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }
  }

  @Nested
  inner class PostsControllerTests {
    @Test
    fun `when POSTS_API GET with defaults cursor and search and valid headers then posts and 200`() {
      val response = doGet<PostsDto>("$POSTS_API/?cursor=&search=")
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.posts?.count()).isEqualTo(20)
    }

    @Test
    fun `when POSTS_API GET with date cursor and default search and valid headers then posts and 200`() {
      val response = doGet<PostsDto>("$POSTS_API/?cursor=2020-06-19T20:42:44&search=")
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.posts?.count()).isEqualTo(20)
    }

    @Test
    fun `when POSTS_API GET with default cursor and a search and valid headers then post and 200`() {
      val response = doGet<PostsDto>("$POSTS_API/?cursor=&search=down")
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.posts?.count()).isEqualTo(1)
    }

    @Test
    fun `when POSTS_API GET with cursor and search and valid headers then post and 200`() {
      val response =
        doGet<PostsDto>("$POSTS_API/?cursor=2020-06-19T20:42:44&search=Distributed asymmetric structure")
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.posts?.count()).isEqualTo(1)
    }

    @Test
    fun `when POSTS_API-{id} GET with valid id then post and 200`() {
      val response = doGet<PostDto>("$POSTS_API/1")
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.id).isEqualTo(1)
    }

    @Test
    fun `when POSTS_API-{id} GET with invalid id then 400`() {
      val response = doGet<String>("$POSTS_API/0")
      assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `when POSTS_API POST with valid new post and headers then post and 201`() {
      val postDtoReq = PostDtoReq("Some title", "Some text")
      val response = doExchange<PostDto>(POSTS_API, HttpMethod.POST, postDtoReq, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body?.title).isEqualTo(postDtoReq.title)
    }

    @Test
    fun `when POSTS_API POST with valid new post but no headers then 401`() {
      val postDtoReq = PostDtoReq("Some title", "Some text")
      val response = doExchange<String>(POSTS_API, HttpMethod.POST, postDtoReq, false)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when POSTS_API POST with invalid new post but valid headers then 400`() {
      val response = doExchange<String>(POSTS_API, HttpMethod.POST, PostDtoReq("", ""), true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
      assertThat(validJsons(response.body,
        mapOf("$.title" to "size must be between 1 and 50",
          "$.text" to "size must be between 1 and 255")))
    }

    @Test
    fun `when POSTS_API PUT with valid id, update post and headers then post and 200`() {
      val postDtoReq = PostDtoReq("Updated title", "Updated text")
      val response = doExchange<PostDto>("$POSTS_API/8", HttpMethod.PUT, postDtoReq, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.title).isEqualTo(postDtoReq.title)
    }

    @Test
    fun `when POSTS_API PUT with valid id, update post but invalid headers then 401`() {
      val response = doExchange<String>("$POSTS_API/8",
        HttpMethod.PUT,
        PostDtoReq("Updated title", "Updated text"),
        false)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when POSTS_API PUT with valid id, headers but invalid update post then 400`() {
      val response = doExchange<String>("$POSTS_API/8", HttpMethod.PUT, PostDtoReq("", ""), true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
      assertThat(validJsons(response.body,
        mapOf("$.title" to "size must be between 1 and 50",
          "$.text" to "size must be between 1 and 255")))
    }

    @Test
    fun `when POSTS_API PUT with invalid id but valid headers and update post then 404`() {
      val response = doExchange<String>("$POSTS_API/0",
        HttpMethod.PUT,
        PostDtoReq("Updated title", "Updated text"),
        true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `when POSTS_API PUT with valid id, update post and headers but different user in token and post then 400`() {
      val response = doExchange<String>("$POSTS_API/2",
        HttpMethod.PUT,
        PostDtoReq("Updated title", "Updated text"),
        true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `when POSTS_API-{id} DELETE with valid id and headers then 204`() {
      val response = doExchange<String>("$POSTS_API/8", HttpMethod.DELETE, null, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `when POSTS_API-{id} DELETE with invalid id but valid headers then 404`() {
      val response = doExchange<String>("$POSTS_API/0", HttpMethod.DELETE, null, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `when POSTS_API-{id} DELETE with valid id but no headers then 401`() {
      val response = doExchange<String>("$POSTS_API/8", HttpMethod.DELETE, null, false)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when POSTS_API-{id} DELETE with valid id and headers but different user in token and post then 400`() {
      val response = doExchange<String>("$POSTS_API/2",
        HttpMethod.PUT,
        PostDtoReq("Updated title", "Updated text"),
        true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
  }

  @Nested
  inner class VotesControllerTests {
    @Test
    fun `when VOTES_API POST with up vote after a down vote and valid headers then vote and 201`() {
      val response =
        doExchange<String>(VOTES_API, HttpMethod.POST, VoteCreateDtoReq(4, false), true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body).isEqualTo("47")
      val response2 =
        doExchange<String>(VOTES_API, HttpMethod.POST, VoteCreateDtoReq(4, true), true)
      assertThat(response2.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response2.body).isEqualTo("49")
    }

    @Test
    fun `when VOTES_API POST with down vote after a up vote and valid headers then vote and 201`() {
      val response =
        doExchange<String>(VOTES_API, HttpMethod.POST, VoteCreateDtoReq(3, true), true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body).isEqualTo("-41")
      val response2 =
        doExchange<String>(VOTES_API, HttpMethod.POST, VoteCreateDtoReq(3, false), true)
      assertThat(response2.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response2.body).isEqualTo("-43")
    }

    @Test
    fun `when VOTES_API POST with valid vote but no headers then 401`() {
      val response =
        doExchange<String>(VOTES_API, HttpMethod.POST, VoteCreateDtoReq(4, true), false)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when VOTES_API POST with invalid vote but valid headers then 0 and 201`() {
      val response = doExchange<String>(VOTES_API, HttpMethod.POST, VoteCreateDtoReq(0, true), true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body).isEqualTo("0")
    }
  }

  @Nested
  inner class FollowsControllerTests {
    @Test
    fun `when FOLLOWS_API POST with follow and headers then Followed and 201`() {
      val response = doExchange<String>(FOLLOWS_API, HttpMethod.POST, FollowCreateDtoReq(2), true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body).isEqualTo("Followed")
    }

    @Test
    fun `when FOLLOWS_API POST with follow and no headers then 401`() {
      val response = doExchange<String>(FOLLOWS_API, HttpMethod.POST, FollowCreateDtoReq(2), false)
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when FOLLOWS_API POST with follow and unfollow and headers then Followed, Unfollowed and 201`() {
      val response = doExchange<String>(FOLLOWS_API, HttpMethod.POST, FollowCreateDtoReq(4), true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body).isEqualTo("Followed")
      val response2 = doExchange<String>(FOLLOWS_API, HttpMethod.POST, FollowCreateDtoReq(4), true)
      assertThat(response2.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response2.body).isEqualTo("Unfollowed")
    }

    @Test
    fun `when FOLLOWS_API GET with valid fromID and headers then follows 2 and 200`() {
      doExchange<String>(FOLLOWS_API, HttpMethod.POST, FollowCreateDtoReq(5), true)
      doExchange<String>(FOLLOWS_API, HttpMethod.POST, FollowCreateDtoReq(6), true)
      val response = doExchange<Array<UserDto>>("$FOLLOWS_API/1", HttpMethod.GET, null, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.size).isEqualTo(2)
    }

    @Test
    fun `when FOLLOWS_API GET with valid fromID but no headers then 401`() {
      val response = doGet<String>("$FOLLOWS_API/1")
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `when FOLLOWS_API-to GET with valid toID and headers then follows 1 and 200`() {
      doExchange<String>(FOLLOWS_API, HttpMethod.POST, FollowCreateDtoReq(1), getMuireAuthHeaders())
      val response = doExchange<Array<UserDto>>("$FOLLOWS_API/to/1", HttpMethod.GET, null, true)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body?.size).isEqualTo(1)
    }

    @Test
    fun `when FOLLOWS_API-to GET with valid toID but no headers then 401`() {
      val response = doGet<String>("$FOLLOWS_API/to/1")
      assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }
  }

  private inline fun <reified T> doPost(api: String, request: Any) =
    testRestTemplate.postForEntity<T>(
      api,
      request,
      T::class.java
    )

  private inline fun <reified T> doGet(api: String) =
    testRestTemplate.getForEntity(api, T::class.java)

  private fun validJsons(json: String?, pathToOther: Map<String, String>): Boolean {
    for ((path, other) in pathToOther) {
      if (!JsonPath.parse(json).read<String>(path).equals(other)) return false
    }
    return true
  }

  private inline fun <reified T> doExchange(
    api: String,
    method: HttpMethod,
    request: Any?,
    isAuth: Boolean,
  ) =
    testRestTemplate.exchange(
      api,
      method,
      HttpEntity<Any>(request, if (isAuth) getBobAuthHeaders() else null),
      T::class.java
    )

  private inline fun <reified T> doExchange(
    api: String,
    method: HttpMethod,
    request: Any?,
    headers: MultiValueMap<String, String>,
  ) =
    testRestTemplate.exchange(
      api,
      method,
      HttpEntity<Any>(request, headers),
      T::class.java
    )

  private fun getBobAuthHeaders(): HttpHeaders {
    val headers = HttpHeaders()
    headers[X_AUTH_TOKE] =
      "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiQm9iIiwiaWQiOjEsImVtYWlsIjoiYm9iQGJvYi5jb20iLCJzdWIiOiJib2JAYm9iLmNvbSIsImlhdCI6MTYwMjI3OTE5NywiZXhwIjoxOTE3Njc5MTk3fQ.4sV4C_GE8QcedIZHgllu9s6FTq8xsJIwxqFcg5xfVHU"
    return headers
  }

  private fun getMuireAuthHeaders() = HttpHeaders().apply {
    set(X_AUTH_TOKE,
      "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiTXVpcmUiLCJpZCI6NSwiZW1haWwiOiJtZWxpczRAdWNzZC5lZHUiLCJzdWIiOiJtZWxpczRAdWNzZC5lZHUiLCJpYXQiOjE2MDM3NTM4MjQsImV4cCI6MTkxOTE1MzgyNH0.pU9XxVSJ8ai5iXwfvVN5KjmwaN1mfeDZzgdSUld9K7g")
  }
}