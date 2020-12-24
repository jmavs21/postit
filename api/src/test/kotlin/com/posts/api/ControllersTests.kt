package com.posts.api

import com.posts.api.conf.X_AUTH_TOKEN
import com.posts.api.follows.FOLLOWED
import com.posts.api.follows.UNFOLLOWED
import com.posts.api.web.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

const val TOTAL_USERS = 30
const val TOTAL_POSTS = 100
const val LOCATION = "location"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllersTests {

  @Autowired
  lateinit var testRestTemplate: TestRestTemplate

  @Nested
  inner class AuthControllerTests {
    @Test
    fun `when AUTH_API POST with valid email and password then token and 200`() {
      val response = doPost<String>(AUTH_API, AuthDtoReq(Bob.email, Bob.password))
      check200(response.statusCode)
      checkStringContains(response.body, ".")
    }

    @Test
    fun `when AUTH_API POST with invalid email then 400`() {
      val response = doPost<String>(AUTH_API, AuthDtoReq("bob2@bob.com", Bob.password))
      check400(response.statusCode)
      checkJsons(response.body, mapOf("$.email" to "the email doesn't exists"))
    }

    @Test
    fun `when AUTH_API POST with invalid password then 400`() {
      val response = doPost<String>(AUTH_API, AuthDtoReq(Bob.email, "bob2"))
      check400(response.statusCode)
      checkJsons(response.body, mapOf("$.password" to "incorrect email or password"))
    }

    @Test
    fun `when AUTH_API POST with invalid email and password size then 400`() {
      val response = doPost<String>(AUTH_API, AuthDtoReq("", ""))
      check400(response.statusCode)
      checkJsons(response.body, mapOf("$.email" to "size must be between 1 and 50",
        "$.password" to "size must be between 1 and 50"))
    }
  }

  @Nested
  inner class UsersControllerTests {
    @Test
    fun `when USERS_API GET then users and 200`() {
      val response = doGet<Array<UserDto>>(USERS_API)
      check200(response.statusCode)
      checkIntEqual(response.body?.size, TOTAL_USERS)
    }

    @Test
    fun `when USERS_API-{id} GET with valid id and headers then user and 200`() {
      val response = doExchange<UserDto>("$USERS_API/${Bob.id}", HttpMethod.GET, null, true)
      check200(response.statusCode)
      checkStringContains(response.body?.name, Bob.name)
    }

    @Test
    fun `when USERS_API-{id} GET with valid id but no header then 401`() {
      val response = doGet<String>("$USERS_API/${Bob.id}")
      check401(response.statusCode)
    }

    @Test
    fun `when USERS_API-{id} GET with invalid id but valid headers then 404`() {
      val response = doExchange<String>("$USERS_API/0", HttpMethod.GET, null, true)
      check404(response.statusCode)
    }

    @Test
    fun `when USERS_API-me GET with valid headers then user and 200`() {
      val response = doExchange<UserDto>("$USERS_API/me", HttpMethod.GET, null, true)
      check200(response.statusCode)
      checkStringContains(response.body?.name, Bob.name)
    }

    @Test
    fun `when USERS_API-me GET with no headers then 401`() {
      val response = doGet<String>("$USERS_API/me")
      check401(response.statusCode)
    }

    @Test
    fun `when USERS_API POST with valid new user then user token and 200`() {
      val userDtoReq = UserCreateDtoReq("Zack", "zack@mail.com", "zack")
      val response = doPost<UserDto>(USERS_API, userDtoReq)
      check201(response.statusCode)
      checkStringContains(response.body?.name, userDtoReq.name)
      checkStringContains(response.headers[X_AUTH_TOKEN]?.get(0), ".")
      checkStringContains(response.headers[LOCATION]?.get(0), "$USERS_API/${response.body?.id}")
      deleteCreatedUser(response.body?.id)
    }

    @Test
    fun `when USERS_API POST with invalid name, email and password size for new user then 400`() {
      val response = doPost<String>(USERS_API, UserCreateDtoReq("", "", ""))
      check400(response.statusCode)
      checkJsons(response.body,
        mapOf("$.name" to "size must be between 1 and 50",
          "$.email" to "size must be between 1 and 50",
          "$.password" to "size must be between 1 and 50"))
    }

    @Test
    fun `when USERS_API-{id} PUT with valid update user and headers then user token and 200`() {
      val user = createUser()
      val userDtoReq = UserUpdateDtoReq("UpdatedName")
      val response =
        doExchange<UserDto>("$USERS_API/${user.id}", HttpMethod.PUT, userDtoReq, true)
      check200(response.statusCode)
      checkStringContains(response.body?.name, userDtoReq.name)
      checkStringContains(response.headers[X_AUTH_TOKEN]?.get(0), ".")
      deleteCreatedUser(user.id)
    }

    @Test
    fun `when USERS_API-{id} PUT with invalid name but valid headers for update user then 400`() {
      val response =
        doExchange<String>("$USERS_API/${Bob.id}", HttpMethod.PUT, UserUpdateDtoReq(""), true)
      check400(response.statusCode)
      checkJsons(response.body, mapOf("$.name" to "size must be between 1 and 50"))
    }

    @Test
    fun `when USERS_API-{id} PUT with valid name but invalid headers for update user then 401`() {
      val response =
        doExchange<String>("$USERS_API/${Bob.id}", HttpMethod.PUT, UserUpdateDtoReq("Bobby"), false)
      check401(response.statusCode)
    }

    @Test
    fun `when USERS_API-{id} DELETE with valid id and headers then 204`() {
      val user = createUser()
      val response =
        doExchange<String>("$USERS_API/${user.id}", HttpMethod.DELETE, null, true)
      check204(response.statusCode)
      check204(response.statusCode)
    }

    @Test
    fun `when USERS_API-{id} DELETE with invalid id but valid headers then 404`() {
      val response = doExchange<String>("$USERS_API/0", HttpMethod.DELETE, null, true)
      check404(response.statusCode)
    }

    @Test
    fun `when USERS_API-{id} DELETE with valid id but no headers then 401`() {
      val response = doExchange<String>("$USERS_API/${TOTAL_USERS}", HttpMethod.DELETE, null, false)
      check401(response.statusCode)
    }
  }

  @Nested
  inner class PostsControllerTests {
    @Test
    fun `when POSTS_API GET with defaults cursor and search and valid headers then posts and 200`() {
      val response = doGet<PostFeedDto>("$POSTS_API/?cursor=&search=")
      check200(response.statusCode)
      checkIntEqual(response.body?.posts?.count(), POSTS_LIMIT)
      checkStringContains(response.body?.posts?.first()?.title,
        "Down-sized incremental application")
    }

    @Test
    fun `when POSTS_API GET with date cursor and default search and valid headers then posts and 200`() {
      val response = doGet<PostFeedDto>("$POSTS_API/?cursor=2020-06-19T20:42:44&search=")
      check200(response.statusCode)
      checkIntEqual(response.body?.posts?.count(), POSTS_LIMIT)
      checkStringContains(response.body?.posts?.first()?.title, "Distributed asymmetric structure")
    }

    @Test
    fun `when POSTS_API GET with default cursor and a search and valid headers then post and 200`() {
      val response = doGet<PostFeedDto>("$POSTS_API/?cursor=&search=down")
      check200(response.statusCode)
      checkIntEqual(response.body?.posts?.count(), 1)
      checkStringContains(response.body?.posts?.first()?.title,
        "Down-sized incremental application")
    }

    @Test
    fun `when POSTS_API GET with cursor and search and valid headers then post and 200`() {
      val response =
        doGet<PostFeedDto>("$POSTS_API/?cursor=2020-06-19T20:42:44&search=Distributed asymmetric structure")
      check200(response.statusCode)
      checkIntEqual(response.body?.posts?.count(), 1)
      checkStringContains(response.body?.posts?.first()?.title, "Distributed asymmetric structure")
    }

    @Test
    fun `when POSTS_API-{id} GET with valid id then post and 200`() {
      val response = doGet<PostDto>("$POSTS_API/1")
      check200(response.statusCode)
      checkLongEqual(response.body?.id, 1L)
      checkStringContains(response.body?.title, "Devolved regional parallelism")
    }

    @Test
    fun `when POSTS_API-{id} GET with invalid id then 400`() {
      val response = doGet<String>("$POSTS_API/0")
      check404(response.statusCode)
    }

    @Test
    fun `when POSTS_API POST with valid new post and headers then post and 201`() {
      val postDtoReq = PostDtoReq("Some title", "Some text")
      val response = doExchange<PostDto>(POSTS_API, HttpMethod.POST, postDtoReq, true)
      check201(response.statusCode)
      checkStringContains(response.body?.title, postDtoReq.title)
      checkStringContains(response.body?.text, postDtoReq.text)
      checkStringContains(response.headers[LOCATION]?.get(0), "$POSTS_API/${response.body?.id}")
      deleteCreatedPost(response.body?.id)
    }

    @Test
    fun `when POSTS_API POST with valid new post but no headers then 401`() {
      val response =
        doExchange<String>(POSTS_API, HttpMethod.POST, PostDtoReq("Some title", "Some text"), false)
      check401(response.statusCode)
    }

    @Test
    fun `when POSTS_API POST with invalid new post but valid headers then 400`() {
      val response = doExchange<String>(POSTS_API, HttpMethod.POST, PostDtoReq("", ""), true)
      check400(response.statusCode)
      checkJsons(response.body,
        mapOf("$.title" to "size must be between 1 and 50",
          "$.text" to "size must be between 1 and 500"))
    }

    @Test
    fun `when POSTS_API PUT with valid id, update post and headers then post and 200`() {
      val post = createPost()
      val postDtoReq = PostDtoReq("Updated title", "Updated text")
      val response =
        doExchange<PostDto>("$POSTS_API/${post.id}", HttpMethod.PUT, postDtoReq, true)
      check200(response.statusCode)
      checkStringContains(response.body?.title, postDtoReq.title)
      checkStringContains(response.body?.text, postDtoReq.text)
      deleteCreatedPost(response.body?.id)
    }

    @Test
    fun `when POSTS_API PUT with valid id, update post but invalid headers then 401`() {
      val response = doExchange<String>("$POSTS_API/1",
        HttpMethod.PUT,
        PostDtoReq("Updated title", "Updated text"),
        false)
      check401(response.statusCode)
    }

    @Test
    fun `when POSTS_API PUT with valid id, headers but invalid update post then 400`() {
      val response = doExchange<String>("$POSTS_API/1", HttpMethod.PUT, PostDtoReq("", ""), true)
      check400(response.statusCode)
      checkJsons(response.body,
        mapOf("$.title" to "size must be between 1 and 50",
          "$.text" to "size must be between 1 and 500"))
    }

    @Test
    fun `when POSTS_API PUT with invalid id but valid headers and update post then 404`() {
      val response = doExchange<String>("$POSTS_API/0",
        HttpMethod.PUT,
        PostDtoReq("Updated title", "Updated text"),
        true)
      check404(response.statusCode)
    }

    @Test
    fun `when POSTS_API PUT with valid id, update post and headers but different user in token and post then 400`() {
      val response = doExchange<String>("$POSTS_API/1",
        HttpMethod.PUT,
        PostDtoReq("Updated title", "Updated text"),
        true)
      check400(response.statusCode)
      checkStringContains(response.body, "Needs same user as creator of post to update.")
    }

    @Test
    fun `when POSTS_API-{id} DELETE with valid id and headers then 204`() {
      val post = createPost()
      val response =
        doExchange<String>("$POSTS_API/${post.id}", HttpMethod.DELETE, null, true)
      check204(response.statusCode)
    }

    @Test
    fun `when POSTS_API-{id} DELETE with invalid id but valid headers then 404`() {
      val response = doExchange<String>("$POSTS_API/0", HttpMethod.DELETE, null, true)
      check404(response.statusCode)
    }

    @Test
    fun `when POSTS_API-{id} DELETE with valid id but no headers then 401`() {
      val response = doExchange<String>("$POSTS_API/1", HttpMethod.DELETE, null, false)
      check401(response.statusCode)
    }

    @Test
    fun `when POSTS_API-{id} DELETE with valid id and headers but different user in token and post then 400`() {
      val response = doExchange<String>("$POSTS_API/1",
        HttpMethod.DELETE,
        PostDtoReq("Updated title", "Updated text"),
        true)
      check400(response.statusCode)
      checkStringContains(response.body, "Needs same user as creator of post to delete.")
    }
  }

  @Nested
  inner class VotesControllerTests {
    @Test
    fun `when VOTES_API POST with up vote after a down vote and valid headers then vote and 201`() {
      val post = createPost()
      val response =
        doExchange<String>(VOTES_API, HttpMethod.POST, VoteCreateDtoReq(post.id!!, false), true)
      check201(response.statusCode)
      checkStringContains(response.body, "-1")
      val response2 =
        doExchange<String>(VOTES_API, HttpMethod.POST, VoteCreateDtoReq(post.id!!, true), true)
      check201(response2.statusCode)
      checkStringContains(response2.body, "1")
      deleteCreatedPost(post.id)
    }

    @Test
    fun `when VOTES_API POST with down vote after a up vote and valid headers then vote and 201`() {
      val post = createPost()
      val response =
        doExchange<String>(VOTES_API, HttpMethod.POST, VoteCreateDtoReq(post.id!!, true), true)
      check201(response.statusCode)
      checkStringContains(response.body, "1")
      val response2 =
        doExchange<String>(VOTES_API, HttpMethod.POST, VoteCreateDtoReq(post.id!!, false), true)
      check201(response2.statusCode)
      checkStringContains(response2.body, "-1")
      deleteCreatedPost(post.id)
    }

    @Test
    fun `when VOTES_API POST with valid vote but no headers then 401`() {
      val response =
        doExchange<String>(VOTES_API, HttpMethod.POST, VoteCreateDtoReq(1, true), false)
      check401(response.statusCode)
    }

    @Test
    fun `when VOTES_API POST with invalid postId but valid headers then 404`() {
      val response = doExchange<String>(VOTES_API, HttpMethod.POST, VoteCreateDtoReq(0, true), true)
      check404(response.statusCode)
    }
  }

  @Nested
  inner class FollowsControllerTests {
    @Test
    fun `when FOLLOWS_API POST with follow and unfollow and headers then Followed, Unfollowed and 201`() {
      val response =
        doExchange<String>(FOLLOWS_API, HttpMethod.POST, FollowCreateDtoReq(Bob.id + 1), true)
      check201(response.statusCode)
      checkStringContains(response.body, FOLLOWED)
      val response2 =
        doExchange<String>(FOLLOWS_API, HttpMethod.POST, FollowCreateDtoReq(Bob.id + 1), true)
      check201(response2.statusCode)
      checkStringContains(response2.body, UNFOLLOWED)
    }

    @Test
    fun `when FOLLOWS_API POST with follow and no headers then 401`() {
      val response = doExchange<String>(FOLLOWS_API, HttpMethod.POST, FollowCreateDtoReq(1), false)
      check401(response.statusCode)
    }

    @Test
    fun `when FOLLOWS_API GET with valid fromID and headers then follows 2 and 200`() {
      follow(Bob.id + 1)
      follow(Bob.id + 2)
      val response =
        doExchange<Array<UserDto>>("$FOLLOWS_API/${Bob.id}", HttpMethod.GET, null, true)
      check200(response.statusCode)
      checkIntEqual(response.body?.size, 2)
      unfollow(Bob.id + 1)
      unfollow(Bob.id + 2)
    }

    @Test
    fun `when FOLLOWS_API GET with valid fromID but no headers then 401`() {
      val response = doGet<String>("$FOLLOWS_API/1")
      check401(response.statusCode)
    }

    @Test
    fun `when FOLLOWS_API-to GET with valid toID and headers then follows 1 and 200`() {
      val user = createUser()
      addFollowerToBob(user)
      val response =
        doExchange<Array<UserDto>>("$FOLLOWS_API/to/${Bob.id}", HttpMethod.GET, null, true)
      check200(response.statusCode)
      checkIntEqual(response.body?.size, 1)
      checkStringContains(response.body?.get(0)?.name, user.name)
      deleteCreatedUser(user.id)
    }

    @Test
    fun `when FOLLOWS_API-to GET with valid toID but no headers then 401`() {
      val response = doGet<String>("$FOLLOWS_API/to/1")
      check401(response.statusCode)
    }

    private fun follow(toId: Long) {
      val response =
        doExchange<String>(FOLLOWS_API, HttpMethod.POST, FollowCreateDtoReq(toId), true)
      check201(response.statusCode)
      checkStringContains(response.body, FOLLOWED)
    }

    private fun unfollow(toId: Long) {
      val response =
        doExchange<String>(FOLLOWS_API, HttpMethod.POST, FollowCreateDtoReq(toId), true)
      check201(response.statusCode)
      checkStringContains(response.body, UNFOLLOWED)
    }
  }

  private object Bob {
    val id = 1L
    val name = "Bob"
    val email = "bob@bob.com"
    val password = "bob"
  }

  private inline fun <reified T> doPost(api: String, request: Any) =
    testRestTemplate.postForEntity<T>(
      api,
      request,
      T::class.java
    )

  private inline fun <reified T> doGet(api: String) =
    testRestTemplate.getForEntity(api, T::class.java)

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

  private fun getBobAuthHeaders(): HttpHeaders {
    val headers = HttpHeaders()
    headers[X_AUTH_TOKEN] =
      "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiQm9iIiwiaWQiOjEsImVtYWlsIjoiYm9iQGJvYi5jb20iLCJzdWIiOiJib2JAYm9iLmNvbSIsImlhdCI6MTYwMjI3OTE5NywiZXhwIjoxOTE3Njc5MTk3fQ.4sV4C_GE8QcedIZHgllu9s6FTq8xsJIwxqFcg5xfVHU"
    return headers
  }

  private fun createUser(): UserDto {
    val response = doPost<UserDto>(USERS_API, UserCreateDtoReq("Tmp", "tmp@tmp.com", Bob.password))
    check201(response.statusCode)
    if (response.body == null) fail("Could not create user.")
    return response.body!!
  }

  private fun deleteCreatedUser(userId: Long?) {
    if (userId == null) fail("Could not delete user.")
    val response =
      doExchange<String>("$USERS_API/${userId}", HttpMethod.DELETE, null, true)
    check204(response.statusCode)
  }

  private fun createPost(): PostDto {
    val response =
      doExchange<PostDto>(POSTS_API, HttpMethod.POST, PostDtoReq("Some title", "Some text"), true)
    check201(response.statusCode)
    if (response.body == null) fail("Could not create post.")
    return response.body!!
  }

  private fun deleteCreatedPost(postId: Long?) {
    val response = doExchange<String>("$POSTS_API/${postId}", HttpMethod.DELETE, null, true)
    check204(response.statusCode)
  }

  private fun addFollowerToBob(user: UserDto) = testRestTemplate.exchange(
    FOLLOWS_API,
    HttpMethod.POST,
    HttpEntity(FollowCreateDtoReq(Bob.id), getUserHeaders(user)),
    String::class.java)

  private fun getUserHeaders(user: UserDto): HttpHeaders {
    val response = doPost<String>(AUTH_API, AuthDtoReq(user.email, Bob.password))
    check200(response.statusCode)
    checkStringContains(response.body, ".")
    return HttpHeaders().apply { set(X_AUTH_TOKEN, response.body) }
  }
}