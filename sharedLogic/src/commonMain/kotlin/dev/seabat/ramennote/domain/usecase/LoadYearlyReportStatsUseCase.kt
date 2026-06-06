package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.domain.model.MonthlyReportCount
import kotlin.time.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class LoadYearlyReportStatsUseCase(
    private val reportsRepository: ReportsRepositoryContract
) : LoadYearlyReportStatsUseCaseContract {
    override suspend operator fun invoke(): List<MonthlyReportCount> {
        val reports = reportsRepository.load()

        // 今日の日付を取得
        val today =
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date

        // 1年前の日付を計算（月初に設定。 今日が2025/09/15の場合は2024/10/01）
        val oneYearAgoDate = today.minus(DatePeriod(months = 11))
        val oneYearAgo = LocalDate(oneYearAgoDate.year, oneYearAgoDate.month, 1)

        // 過去1年間のデータをフィルタリング
        val filteredReports =
            reports.filter { report ->
                report.date != null && report.date >= oneYearAgo && report.date <= today
            }

        // 月別に集計
        val monthlyCounts = mutableMapOf<String, Int>()

        filteredReports.forEach { report ->
            val date = report.date ?: return@forEach
            val yearMonth = "${date.year}-${date.monthNumber.toString().padStart(2, '0')}"
            monthlyCounts[yearMonth] = (monthlyCounts[yearMonth] ?: 0) + 1
        }

        // 過去12ヶ月分のデータを生成（データがない月は0件）
        val result = mutableListOf<MonthlyReportCount>()
        var currentDate = oneYearAgo

        while (currentDate <= today) {
            val yearMonth = "${currentDate.year}-${currentDate.monthNumber.toString().padStart(2, '0')}"
            val count = monthlyCounts[yearMonth] ?: 0
            result.add(MonthlyReportCount(yearMonth = yearMonth, count = count))

            // 次の月へ
            currentDate = currentDate.plus(DatePeriod(months = 1))
        }

        return result
    }
}
