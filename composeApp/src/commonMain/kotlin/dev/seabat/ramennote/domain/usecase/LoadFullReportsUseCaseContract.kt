package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.FullReport

/**
 * 画像データを含めた食レポデータを読み込む
 *
 * - shopId が null の場合は全食レポをリターンする
 * - shopId が null でない場合は、食レポをを shopId でフィルタリングしてからリターンする
 *
 */
interface LoadFullReportsUseCaseContract {
    suspend operator fun invoke(shopId: Int? = null): List<FullReport>
}
