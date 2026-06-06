package dev.seabat.ramennote.config

object UnsplashConfig {
    val ACCESS_KEY = BuildSecrets.UNSPLASH_ACCESS_KEY

    // APIの制限
    const val MAX_REQUESTS_PER_HOUR = 50
    const val MAX_REQUESTS_PER_DAY = 5000
}
