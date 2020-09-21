package com.postit.api.error

import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.validation.ConstraintViolationException


@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

  @ExceptionHandler(ErrorFieldException::class)
  fun handleGlobalExceptions(ex: ErrorFieldException): ResponseEntity<Any> {
    return ResponseEntity(ex.errors, ex.status)
  }

  @ExceptionHandler(ResponseStatusException::class)
  fun handleGlobalExceptions(ex: ResponseStatusException): ResponseEntity<String> {
    return ResponseEntity(ex.message, ex.status)
  }

  @ExceptionHandler(DuplicateKeyException::class)
  fun handleDuplicateKeyException(ex: DuplicateKeyException?): ResponseEntity<String> {
    return ResponseEntity("Error with unique constraint.", HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(ConstraintViolationException::class)
  fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<String> {
    return ResponseEntity("Error with constraint violation: " + ex.message, HttpStatus.BAD_REQUEST)
  }

  override fun handleHttpMediaTypeNotSupported(ex: HttpMediaTypeNotSupportedException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
    return ResponseEntity("Error with media type not supported: " + ex.message, HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  }

  override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
    val errors: MutableMap<String, String?> = HashMap()
    for (error in ex.bindingResult.fieldErrors) {
      errors[error.field] = error.defaultMessage
    }
    return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
  }
}