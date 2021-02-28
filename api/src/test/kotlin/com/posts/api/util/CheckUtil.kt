package com.posts.api.util

import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

fun check200(statusCode: HttpStatus) {
  assertThat(statusCode).isEqualTo(HttpStatus.OK)
}

fun check201(statusCode: HttpStatus) {
  assertThat(statusCode).isEqualTo(HttpStatus.CREATED)
}

fun check204(statusCode: HttpStatus) {
  assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT)
}

fun check400(statusCode: HttpStatus) {
  assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
}

fun check401(statusCode: HttpStatus) {
  assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
}

fun check404(statusCode: HttpStatus) {
  assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND)
}

fun checkStringContains(body: String?, value: String) {
  assertThat(body).contains(value)
}

fun checkIntEqual(first: Int?, second: Int) {
  assertThat(first).isEqualTo(second)
}

fun checkLongEqual(first: Long?, second: Long) {
  assertThat(first).isEqualTo(second)
}

fun checkJsons(json: String?, pathToOther: Map<String, String>) {
  for ((path, other) in pathToOther) {
    checkStringContains(JsonPath.parse(json).read<String>(path), other)
  }
}