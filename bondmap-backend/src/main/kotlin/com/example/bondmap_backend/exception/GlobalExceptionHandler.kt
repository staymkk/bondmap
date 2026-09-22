package com.example.bondmap_backend.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(
        exception: MethodArgumentNotValidException
    ): ValidationErrorResponse {

        val errors = exception.bindingResult
            .fieldErrors
            .associate {
                it.field to (it.defaultMessage ?: "Invalid value")
            }

        return ValidationErrorResponse(
            message = "Validation failed",
            errors = errors
        )
    }

    @ExceptionHandler(BondNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleBondNotFoundException(
        exception: BondNotFoundException
    ): ErrorResponse {
        return ErrorResponse(
            message = exception.message ?: "Bond not found"
        )
    }
}

data class ValidationErrorResponse(
    val message: String,
    val errors: Map<String, String>
)

data class ErrorResponse(
    val message: String
)