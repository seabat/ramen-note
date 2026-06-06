package dev.seabat.ramennote.domain.model

sealed class RunStatus<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Idle<T> : RunStatus<T>()

    class Success<T>(
        data: T
    ) : RunStatus<T>(data = data)

    class Error<T>(
        errorMessage: String
    ) : RunStatus<T>(message = errorMessage)

    class Loading<T> : RunStatus<T>()
}
