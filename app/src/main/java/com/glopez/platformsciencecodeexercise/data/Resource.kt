package com.glopez.platformsciencecodeexercise.data

/**
 * Wrapper class around a model to provide state.
 * [Loading]: When the model data is being fetched or determined
 * [Success]: When model data is ready to be consumed by the caller
 * [Error]: When an error is encountered while fetching the model data
 */
sealed class Resource<out T> {
    class Loading<out T> (val data: T? = null) : Resource<T>()
    data class Success<out T> (val data: T?) : Resource<T>()
    data class Error<out T>(val error: Throwable) : Resource<T>()
}