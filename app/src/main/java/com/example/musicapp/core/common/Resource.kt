package com.example.musicapp.core.common

/**
 * Generic wrapper for one-shot async results (network/db calls that can fail).
 * For continuously observed data we use Flow directly instead.
 */
sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>
    data object Loading : Resource<Nothing>
}

inline fun <T> Resource<T>.onSuccess(block: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) block(data)
    return this
}

inline fun <T> Resource<T>.onError(block: (String) -> Unit): Resource<T> {
    if (this is Resource.Error) block(message)
    return this
}
