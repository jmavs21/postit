package com.posts.api

import com.posts.api.repo.PostRepo
import com.posts.api.repo.UserRepo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
class ControllersTests(@Autowired val mockMvc: MockMvc) {

  @MockBean
  lateinit var userRepo: UserRepo

  @MockBean
  lateinit var postRepo: PostRepo

  @Test
  fun `list users`() {
    mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk)
  }
}