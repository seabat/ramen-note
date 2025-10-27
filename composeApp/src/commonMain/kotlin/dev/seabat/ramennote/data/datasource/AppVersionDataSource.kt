package dev.seabat.ramennote.data.datasource

expect class AppVersionDataSource() {
    fun getVersionName(): String

    fun getVersionCode(): Int
}
