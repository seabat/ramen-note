package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.FullReport

/**
 * 画像データを含めた全食レポデータを読み込む
 */
interface LoadFullReportsUseCaseContract {
    suspend operator fun invoke(): List<FullReport>
}
