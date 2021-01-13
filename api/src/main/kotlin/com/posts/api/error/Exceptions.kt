package com.posts.api.error

import kotlin.RuntimeException

class FieldException(val errors: Map<String, String>) : RuntimeException()

class DataNotFoundException(val error: String) : RuntimeException()

class ServiceException(val error: String) : RuntimeException()