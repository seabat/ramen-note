# コーディング規約

クリーンアーキテクチャ + MVVM の3層構造（`UI → Domain → Data`）における各要素の実装規約。
各層は **Contract（インターフェース）** を介して依存する（実装クラスを直接参照しない）。

## 各レイヤーの規約

- **ViewModel**: Contract + 実装 + Mock の3点セット。状態は `MutableStateFlow`（private）+ `StateFlow`（public）。UseCase の Contract に依存する
- **UseCase**: Contract + 実装。単一責任（1操作）。`operator fun invoke()` で呼び出し。結果は `RunStatus<T>` でラップ
- **Repository**: Contract + 実装。Entity ↔ Domain モデル変換を担当。DataSource の Contract に依存する
- **DataSource**: プラットフォーム固有処理は `expect/actual` + Contract パターン（詳細は `.claude/rules/platform-specific.md`）
- **Database（Room）**: マイグレーション は `DataModule.common.kt` に記述。現在のバージョン: 6（AreaEntity, ShopEntity, ReportEntity）

## ファイル命名規則

- **expect/actual**: `Xxxx.common.kt` / `Xxxx.android.kt` / `Xxxx.ios.kt`
- **ViewModel**: `XxxxViewModelContract.kt` / `XxxxViewModel.kt` / `MockXxxxViewModel.kt`
- **UseCase**: `XxxxUseCaseContract.kt` / `XxxxUseCase.kt`
- **Repository**: `XxxxRepositoryContract.kt` / `XxxxRepository.kt`
