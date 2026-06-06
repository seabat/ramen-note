package dev.seabat.ramennote.domain.model

import dev.seabat.ramennote.domain.util.createTodayLocalDate
import kotlinx.datetime.LocalDate

/**
 * 食レポの全情報を保持するドメインモデル。
 *
 * 店舗・メニュー・評価など食レポの基本情報に加え、
 * 画像を [imageBytes] または [imagePath] のいずれかの形式で保持する。
 * 用途に応じて使い分ける。
 *
 * @property id 食レポの一意識別子。新規作成時は 0。
 * @property shopId 紐づく店舗の ID。
 * @property shopName 紐づく店舗の名称。
 * @property menuName 食べたメニューの名称。
 * @property photoName ローカルストレージに保存された画像ファイル名。
 * @property impression 食レポの感想テキスト。
 * @property date 食レポの日付。
 * @property imageBytes 画像のバイト配列。レポート編集画面で SharedImage を生成する際に使用する。
 * @property imagePath 画像のファイルパス。一覧表示画面で Coil が非同期ロードする際に使用する。
 * @property star 評価（0〜5）。
 * @property areaId 紐づくエリアの ID。
 */
data class FullReport(
    val id: Int = 0,
    val shopId: Int = 0,
    val shopName: String = "",
    val menuName: String = "",
    val photoName: String = "",
    val impression: String = "",
    val date: LocalDate = createTodayLocalDate(),
    val imageBytes: ByteArray? = null,
    val imagePath: String? = null,
    val star: Int = 0,
    val areaId: Int = 0
)
