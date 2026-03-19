package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.FullReport

/**
 * 画像データを含めた食レポデータを shopId でフィルタリングして読み込む
 */
interface LoadFullReportsByShopUseCaseContract {
    suspend operator fun invoke(shopId: Int): List<FullReport>
}
