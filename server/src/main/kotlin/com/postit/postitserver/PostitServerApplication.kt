package com.postit.postitserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PostitServerApplication

fun main(args: Array<String>) {
	runApplication<PostitServerApplication>(*args)
}
