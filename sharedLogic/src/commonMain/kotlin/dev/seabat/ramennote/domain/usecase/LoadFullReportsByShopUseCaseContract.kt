package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.FullReport
import kotlinx.coroutines.flow.Flow

/**
 * 画像データを含めた食レポデータを shopId でフィルタリングして読み込む
 */
interface LoadFullReportsByShopUseCaseContract {
    operator fun invoke(shopId: Int): Flow<FullReport>
}
