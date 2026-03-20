package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.FullReport
import kotlinx.coroutines.flow.Flow

/**
 * 画像データを含めた全食レポデータを読み込む
 */
interface LoadFullReportsUseCaseContract {
    operator fun invoke(): Flow<FullReport>
}
