package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.FullReport

/**
 * 画像データを含めた食レポデータを areaId でフィルタリングして読み込む
 */
interface LoadFullReportsByAreaUseCaseContract {
    suspend operator fun invoke(areaId: Int): List<FullReport>
}
