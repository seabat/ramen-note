package dev.seabat.ramennote.ui.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.MonthlyReportCount
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

const val CHART_HEIGHT = 160

/**
 * 過去1年間の月別訪問回数を積み上げグラフで表示するコンポーネント
 *
 * @param monthlyData 月別の訪問回数データ
 * @param modifier Modifier
 */
@Composable
fun StackedBarChart(
    monthlyData: List<MonthlyReportCount>,
    modifier: Modifier = Modifier
) {
    if (monthlyData.isEmpty()) {
        Box(
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(CHART_HEIGHT.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(10.dp)
                    ).border(
                        2.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(10.dp)
                    ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "データがありません",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val maxCount = monthlyData.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1
    val chartHeight = CHART_HEIGHT.dp
    val minBarWidth = 16.dp // 最小棒グラフ幅
    val minBarSpacing = 4.dp // 最小スペース
    val padding = 8.dp

    // MaterialThemeの色を取得（Canvas内で使用するため）
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(10.dp)
                ).border(
                    2.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(10.dp)
                ).padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // グラフエリア
            BoxWithConstraints(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(chartHeight)
            ) {
                val canvasWidth = maxWidth
                val canvasHeight = chartHeight
                
                // 利用可能な幅を最大限活用するように、棒グラフの幅とスペースを動的に計算
                val spacingCount = (monthlyData.size - 1).coerceAtLeast(1)
                
                // 棒グラフとスペースの比率を設定（棒グラフ:スペース = 2:1）
                val barRatio = 2.0f
                val spacingRatio = 1.0f
                val totalRatio = barRatio * monthlyData.size + spacingRatio * spacingCount
                
                // 利用可能な幅を比率に基づいて分配
                val barWidthCalculated = (canvasWidth * barRatio / totalRatio).coerceAtLeast(minBarWidth)
                val barSpacingCalculated = (canvasWidth * spacingRatio / totalRatio).coerceAtLeast(minBarSpacing)

                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val canvasWidthPx = size.width
                    val canvasHeightPx = size.height
                    
                    // 利用可能な幅を最大限活用するように、棒グラフの幅とスペースを動的に計算
                    // 棒グラフとスペースの比率を設定（棒グラフ:スペース = 2:1）
                    val barRatio = 2.0f
                    val spacingRatio = 1.0f
                    val totalRatio = barRatio * monthlyData.size + spacingRatio * spacingCount
                    
                    // 利用可能な幅を比率に基づいて分配
                    val barWidthPx = (canvasWidthPx * barRatio / totalRatio).coerceAtLeast(minBarWidth.toPx())
                    val barSpacingPx = (canvasWidthPx * spacingRatio / totalRatio).coerceAtLeast(minBarSpacing.toPx())

                    // 実際に使用した棒グラフの総幅
                    val totalBarWidthPx = barWidthPx * monthlyData.size
                    // グラフ全体の幅を計算（利用可能な幅いっぱいまで使用）
                    val totalGraphWidth = totalBarWidthPx + barSpacingPx * spacingCount
                    // 左端から配置（余白を最小化）
                    val startOffset = 0f

                    // Y軸の目盛り線を描画
                    val yAxisLineCount = 5
                    for (i in 0..yAxisLineCount) {
                        val y = canvasHeightPx - (canvasHeightPx / yAxisLineCount * i)

                        // グリッド線
                        drawLine(
                            color = outlineColor.copy(alpha = 0.3f),
                            start = Offset(0f, y),
                            end = Offset(canvasWidthPx, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // 各月の棒グラフを描画
                    monthlyData.forEachIndexed { index, data ->
                        val x = startOffset + (barWidthPx + barSpacingPx) * index
                        // 0件の時も最小の高さ（4.dp）で表示

                        val barHeight =
                            if (data.count == 0) {
                                4.dp.toPx()
                            } else {
                                (canvasHeightPx * data.count / maxCount).coerceAtLeast(4.dp.toPx())
                            }
                        val y = canvasHeightPx - barHeight

                        // 0件の時は透明、それ以外は通常の色
                        val barColor =
                            if (data.count == 0) {
                                primaryColor.copy(alpha = 0f)
                            } else {
                                primaryColor
                            }

                        // 棒グラフを描画
                        drawRect(
                            color = barColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidthPx, barHeight),
                            style = Fill
                        )

                        // 棒グラフの枠線（0件の時は透明）
                        val borderColor =
                            if (data.count == 0) {
                                primaryColor.copy(alpha = 0f)
                            } else {
                                primaryColor.copy(alpha = 0.8f)
                            }
                        drawRect(
                            color = borderColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidthPx, barHeight),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
                }

                // 各棒グラフの上にカウント数を表示
                Box(modifier = Modifier.fillMaxSize()) {
                    // グラフ全体の幅を計算（利用可能な幅いっぱいまで使用）
                    val totalGraphWidth = barWidthCalculated * monthlyData.size + barSpacingCalculated * spacingCount
                    // 左端から配置（余白を最小化）
                    val startOffset = 0.dp

                    monthlyData.forEachIndexed { index, data ->
                        val barHeight = (canvasHeight * data.count / maxCount).coerceAtLeast(4.dp)
                        val barX = startOffset + (barWidthCalculated + barSpacingCalculated) * index
                        val barTop = canvasHeight - barHeight

                        // 棒グラフの上20dpに表示。ただし、棒グラフが高すぎる場合は棒グラフの内部（上部）に表示
                        val textY =
                            if (barTop >= 20.dp) {
                                barTop - 20.dp // 棒グラフの上20dp
                            } else {
                                // 棒グラフが高い場合は、棒グラフの内部（上から10dpの位置）に表示
                                barTop + 10.dp
                            }.coerceAtLeast(0.dp)

                        val textX = barX + (barWidthCalculated / 2) - 8.dp // 棒グラフの中央（テキストの幅を考慮）

                        Box(
                            modifier =
                                Modifier
                                    .offset(x = textX, y = textY)
                                    .width(barWidthCalculated),
                            contentAlignment = Alignment.Center
                        ) {
                            // 棒グラフの内部に表示する場合は、テキストの色を白にして視認性を確保
                            val textColor =
                                if (barTop < 20.dp && data.count > 0) {
                                    MaterialTheme.colorScheme.onPrimary // 棒グラフの内部の場合は白
                                } else {
                                    MaterialTheme.colorScheme.primary // 通常はプライマリカラー
                                }

                            Text(
                                text = data.count.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }

            // X軸のラベル（月）
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val canvasWidth = maxWidth
                
                // 利用可能な幅を最大限活用するように、棒グラフの幅とスペースを動的に計算
                val spacingCount = (monthlyData.size - 1).coerceAtLeast(1)
                
                // 棒グラフとスペースの比率を設定（棒グラフ:スペース = 2:1）
                val barRatio = 2.0f
                val spacingRatio = 1.0f
                val totalRatio = barRatio * monthlyData.size + spacingRatio * spacingCount
                
                // 利用可能な幅を比率に基づいて分配
                val barWidthCalculated = (canvasWidth * barRatio / totalRatio).coerceAtLeast(minBarWidth)
                val barSpacingCalculated = (canvasWidth * spacingRatio / totalRatio).coerceAtLeast(minBarSpacing)

                // グラフ全体の幅を計算（利用可能な幅いっぱいまで使用）
                val totalGraphWidth = barWidthCalculated * monthlyData.size + barSpacingCalculated * spacingCount
                // 左端から配置（余白を最小化）
                val startOffset = 0.dp

                Box(modifier = Modifier.fillMaxWidth()) {
                    monthlyData.forEachIndexed { index, data ->
                        val monthLabel = data.yearMonth.substring(5, 7) // "MM" 部分を取得
                        val monthNumber = monthLabel.toIntOrNull() ?: 0 // 先頭の0を削除するために数値に変換
                        val barX = startOffset + (barWidthCalculated + barSpacingCalculated) * index
                        val textX = barX + (barWidthCalculated / 2) // 棒グラフの中央

                        Box(
                            modifier =
                                Modifier
                                    .offset(x = textX - 8.dp, y = 0.dp) // テキストの幅を考慮して中央揃え
                                    .width(barWidthCalculated),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = monthNumber.toString(), // 月（先頭の0なし）
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun StackedBarChartPreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            // 過去12ヶ月分のモックデータ
            val mockData =
                listOf(
                    MonthlyReportCount("2024-01", 3),
                    MonthlyReportCount("2024-02", 5),
                    MonthlyReportCount("2024-03", 0),
                    MonthlyReportCount("2024-04", 8),
                    MonthlyReportCount("2024-05", 4),
                    MonthlyReportCount("2024-06", 6),
                    MonthlyReportCount("2024-07", 10),
                    MonthlyReportCount("2024-08", 7),
                    MonthlyReportCount("2024-09", 5),
                    MonthlyReportCount("2024-10", 9),
                    MonthlyReportCount("2024-11", 6),
                    MonthlyReportCount("2024-12", 4)
                )
            StackedBarChart(mockData)
        }
    }
}

@Preview
@Composable
fun StackedBarChartEmptyPreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            // データがない場合のプレビュー
            StackedBarChart(emptyList())
        }
    }
}

@Preview
@Composable
fun StackedBarChartSmallDataPreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            // データが少ない場合のプレビュー
            val mockData =
                listOf(
                    MonthlyReportCount("2024-11", 1),
                    MonthlyReportCount("2024-12", 3)
                )
            StackedBarChart(mockData)
        }
    }
}
