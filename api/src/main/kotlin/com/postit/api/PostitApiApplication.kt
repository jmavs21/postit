package com.postit.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PostitApiApplication

fun main(args: Array<String>) {
	runApplication<PostitApiApplication>(*args)
}
