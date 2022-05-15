package com.jyodroid.jobsity.model.dto

import com.jyodroid.jobsity.api.networkresponse.NetworkResponse

sealed class Result<T> {
    data class Success<T>(var data: T) : Result<T>()
    data class Failure<T>(val e: NetworkResponse.ApiError<ErrorResponse>) : Result<T>()

    data class UnknownFailure<T>(val e: Throwable?) : Result<T>()

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> failure(e: NetworkResponse.ApiError<ErrorResponse>): Result<T> = Failure(e)
        fun <T> failure(e: Throwable): Result<T> = UnknownFailure(e)
    }

    val extractData: T?
        get() = when (this) {
            is Success -> data
            is Failure -> null
            else -> null
        }
}