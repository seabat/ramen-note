package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.FullReport
import kotlinx.coroutines.flow.Flow

/**
 * 画像データを含めた食レポデータを areaId でフィルタリングして読み込む
 */
interface LoadFullReportsByAreaUseCaseContract {
    operator fun invoke(areaId: Int): Flow<FullReport>
}
