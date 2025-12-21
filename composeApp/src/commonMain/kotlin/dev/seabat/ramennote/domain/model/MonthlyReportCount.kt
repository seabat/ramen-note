package dev.seabat.ramennote.domain.model

/**
 * 月別の訪問回数を表すデータクラス
 *
 * @property yearMonth 年月（例: "2024-01"）
 * @property count その月の訪問回数
 */
data class MonthlyReportCount(
    val yearMonth: String, // "YYYY-MM" 形式
    val count: Int
)

