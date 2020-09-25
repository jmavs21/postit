package com.posts.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PostsApiApplication

fun main(args: Array<String>) {
	runApplication<PostsApiApplication>(*args)
}
