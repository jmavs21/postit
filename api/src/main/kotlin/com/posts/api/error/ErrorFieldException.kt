package com.postit.api.error

import org.springframework.http.HttpStatus

class ErrorFieldException(val errors: Map<String, String>, val status: HttpStatus) : Exception()