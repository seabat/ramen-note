package dev.seabat.ramennote

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform