package com.posts.api.error

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

/**
 * Global handler of application exceptions with mapping to error messages and codes.
 */
@ControllerAdvice
class ErrorHandler : ResponseEntityExceptionHandler() {

  @ExceptionHandler(FieldException::class)
  fun handleFieldExceptions(ex: FieldException): ResponseEntity<Any> =
    ResponseEntity(ex.errors, HttpStatus.BAD_REQUEST)

  @ExceptionHandler(DataNotFoundException::class)
  fun handleDataNotFoundExceptions(ex: DataNotFoundException): ResponseEntity<Any> =
    ResponseEntity(ex.error, HttpStatus.NOT_FOUND)

  @ExceptionHandler(ServiceException::class)
  fun handleServiceExceptions(ex: ServiceException): ResponseEntity<Any> =
    ResponseEntity(ex.error, HttpStatus.BAD_REQUEST)

  @ExceptionHandler(ResponseStatusException::class)
  fun handleGlobalExceptions(ex: ResponseStatusException): ResponseEntity<String> =
    ResponseEntity(ex.message, ex.status)

  @ExceptionHandler(DuplicateKeyException::class)
  fun handleDuplicateKeyException(ex: DuplicateKeyException?): ResponseEntity<String> =
    ResponseEntity("Error with unique constraint.", HttpStatus.BAD_REQUEST)

  @ExceptionHandler(ConstraintViolationException::class)
  fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<String> =
    ResponseEntity("Error with constraint violation: " + ex.message, HttpStatus.BAD_REQUEST)

  override fun handleHttpMediaTypeNotSupported(
    ex: HttpMediaTypeNotSupportedException,
    headers: HttpHeaders,
    status: HttpStatus,
    request: WebRequest,
  ): ResponseEntity<Any> = ResponseEntity(
    "Error with media type not supported: " + ex.message,
    HttpStatus.UNSUPPORTED_MEDIA_TYPE
  )

  override fun handleMethodArgumentNotValid(
    ex: MethodArgumentNotValidException,
    headers: HttpHeaders,
    status: HttpStatus,
    request: WebRequest,
  ): ResponseEntity<Any> {
    val errors: MutableMap<String, String?> = mutableMapOf()
    for (error in ex.bindingResult.fieldErrors) {
      errors[error.field] = error.defaultMessage
    }
    return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
  }
}