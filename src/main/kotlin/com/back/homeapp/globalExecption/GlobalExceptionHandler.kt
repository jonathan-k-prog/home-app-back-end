package com.back.homeapp.globalExecption

import com.back.homeapp.apiResponse.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        exception: MethodArgumentNotValidException
    ): ResponseEntity<ApiResponse<Nothing>> {
        val errors = exception.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }

        val response = ApiResponse<Nothing>(
            status = "error",
            message = "Validation failed",
            data = null,
            errors = errors
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        exception: ResponseStatusException
    ): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse<Nothing>(
            status = "error",
            message = exception.reason ?: "Request failed",
            data = null,
            errors = null
        )

        return ResponseEntity.status(exception.statusCode).body(response)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableMessage(
        exception: HttpMessageNotReadableException
    ): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse<Nothing>(
            status = "error",
            message = "Invalid request body",
            data = null,
            errors = mapOf("body" to "Invalid JSON or unsupported value")
        )

        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        exception: Exception
    ): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse<Nothing>(
            status = "error",
            message = "Internal server error",
            data = null,
            errors = null
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        exception: MethodArgumentTypeMismatchException
    ): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse<Nothing>(
            status = "error",
            message = "Invalid parameter",
            data = null,
            errors = mapOf(exception.name to "Invalid value")
        )

        return ResponseEntity.badRequest().body(response)
    }
}
