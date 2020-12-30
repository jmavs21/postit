package com.posts.api.error

class FieldException(val errors: Map<String, String>) : Exception()

class DataNotFoundException(val error: String) : Exception()

class ServiceException(val error: String) : Exception()