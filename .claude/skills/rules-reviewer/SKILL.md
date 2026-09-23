---
name: rules-reviewer
description: .claude/rules/ 配下のコーディング規約（coding-conventions / di-koin / navgraph-preview / platform-specific / ai-implementation / secrets）に、現在の差分（git diff）が準拠しているかレビューする。
disable-model-invocation: true
allowed-tools: Read, Glob, Bash
---

# ルール準拠レビュー

`.claude/rules/` に定義されたコーディング規約に、現在の差分が準拠しているかを静的にレビューする。
すべての出力は日本語で行う。

## 対象範囲

以下の6ルールのみを対象とする。それ以外（`ktlint.md` / `dependencies.md`）は
自動化済み・対象外のためレビューしない。

- `coding-conventions.md`
- `di-koin.md`
- `navgraph-preview.md`
- `platform-specific.md`
- `ai-implementation.md`
- `secrets.md`

> `secrets.md` は Hook（`.claude/settings.json` の PreToolUse）が `local.properties` /
> `google-services.json` / `.env` への**編集そのもの**を既にブロックしているが、Hook はファイルパスしか
> 見ないため「コード内への秘密情報のベタ書き」までは防げない。このすり抜けを検知するために
> 対象に含める（他の5ルールとは異なり、ファイルの構造・命名規則ではなく**diffの内容**を確認する）。

---

## ステップ1: 差分の取得

```bash
git status --porcelain
```

- **出力があれば**（未コミットの変更がある）→ `git diff HEAD` でその内容をレビュー対象にする
- **出力がなければ** → `git diff main...HEAD` で現在ブランチと main の差分をレビュー対象にする

```bash
git diff HEAD                  # 未コミットがある場合
git diff main...HEAD           # ない場合（ブランチ全体）
```

> 新規追加ファイル（untracked）は `git diff` に出ないため、`git status --porcelain` の `??` 行から
> 別途対象に含める。

---

## ステップ2: 対象ルールの判定

差分に含まれるファイルパスを、以下のパターンと照合する。**マッチしないルールは「対象外」として
スキップし、チェック項目は列挙しない。**

| ルール | 対象ファイルパターン |
|---|---|
| `coding-conventions` | `*ViewModel.kt` / `*ViewModelContract.kt` / `Mock*ViewModel.kt` / `*UseCase.kt` / `*UseCaseContract.kt` / `*Repository.kt` / `*RepositoryContract.kt` / `*DataSource*.kt` / `*.common.kt` / `*.android.kt` / `*.ios.kt` / Room の Entity・Migration |
| `di-koin` | `ViewModelModule.kt` / `DomainModule.kt` / `DataModule.common.kt` / `UiModule.common.kt`、または新規 ViewModel・UseCase・Repository・DataSource ファイルの追加 |
| `navgraph-preview` | `*Screen.kt` |
| `platform-specific` | `androidMain/` または `iosMain/` 配下の `*.android.kt` / `*.ios.kt`、`iosApp/` 配下の Swift 実装 |
| `ai-implementation` | `ShopAiDataSource*` および Firebase AI Logic 呼び出しを含む UseCase・ViewModel |
| `secrets` | `*Config*.kt` / `*Repository*.kt` / `*DataSource*.kt`（秘密情報を扱いやすい箇所）、`build.gradle.kts`、および `local.properties` / `google-services.json` / `.env` 自体が diff に含まれる場合 |

---

## ステップ3: 該当ルールのチェック実施

対象と判定したルールについてのみ `.claude/rules/<ルール名>.md` を読み込み、以下のチェック項目に沿って
差分の内容を確認する。各項目を ✅ PASS / ❌ FAIL / ⚠️ 要確認 で判定する。

### coding-conventions

- ViewModel: Contract + 実装 + Mock の3点セットが揃っているか
- ViewModel の状態は private `MutableStateFlow` + public `StateFlow` になっているか
- UseCase: Contract + 実装が揃い、単一責任（1操作）か。`operator fun invoke()` で呼び出しているか。
  結果を `RunStatus<T>` でラップしているか
- Repository: Contract + 実装が揃い、Entity ↔ Domain モデル変換を担当しているか
- ファイル命名規則（`Xxxx.common.kt` / `.android.kt` / `.ios.kt`、`XxxxViewModelContract.kt` 等）に
  沿っているか
- 各層が Contract（インターフェース）経由で依存しており、実装クラスを直接参照していないか

### di-koin

- 新規追加された ViewModel・UseCase・Repository・DataSource が、対応する DI モジュールファイルに
  登録されているか
- 登録方法（`viewModel { }` / `single<Contract> { Impl(...) }` 等）が規約通りか

### navgraph-preview

- `@Preview` の import が `androidx.compose.ui.tooling.preview.Preview` か
  （`org.jetbrains.compose.ui.tooling.preview.Preview` になっていないか）
- 画面 Composable に `@NavDestination` と `@NavPreview` が付与されているか

### platform-specific

- androidMain 側（または Contract 実装）を変更した場合、iosMain・`iosApp/` の Swift 実装にも
  同等の変更が差分に含まれているか。片方のみの変更であれば ⚠️ 要確認として指摘する

### ai-implementation

- バックエンドが `GenerativeBackend.agentPlatform(...)` か（`vertexAI()` を使っていないか）
- モデル名・`location = "global"` が明示されているか
- `thinkingConfig { thinkingBudget = 0 }` が設定されているか
- `maxOutputTokens` が設定されているか（絞りすぎで JSON が切れる値になっていないか）
- `responseMimeType = "application/json"` + `responseSchema` で構造化出力しているか
- パース失敗時に例外を投げず、空のドメインモデルを返しているか
- キャッシュ参照 → ミス時のみ API 呼び出し → 成功時のみ保存、の順序になっているか
- 多重実行ガード（実行中の再呼び出し防止）があるか

### secrets

- 追加・変更された行に、API キーやトークンらしき文字列リテラルが直接ハードコードされていないか
  （`BuildSecrets.UNSPLASH_ACCESS_KEY` / `BuildSecrets.GOOGLE_MAPS_API_KEY` のように
  `BuildSecrets.*` 経由の参照になっているか）
- 新しい秘密情報を追加する変更であれば、`local.properties` → `generateBuildSecrets` タスク →
  `BuildSecrets.kt` 自動生成 → `BuildSecrets.XXX_KEY` 参照という既存パターンに従っているか
- diff（`git status --porcelain` の結果を含む）に `local.properties` / `google-services.json` /
  `.env` 自体が追加・変更として含まれていないか（Hook をすり抜けているケースの検知）

---

## ステップ4: 結果の報告

ルールごとに見出しを立て、対象外・PASS・FAIL・要確認を報告する。FAIL・要確認には具体的な
問題箇所（ファイルパス・該当箇所）と修正案を示す。**このスキルでは修正の実施は行わない**
（提示のみ。実際の編集はユーザーの承認を得てから別途行う）。

出力フォーマット例:

```
## coding-conventions
✅ PASS - ShopViewModel は Contract + 実装 + Mock の3点セットが揃っている

## di-koin
❌ FAIL - ShopUseCase が DomainModule.common.kt に未登録
  修正案: single<ShopUseCaseContract> { ShopUseCase(get()) } を追加

## navgraph-preview
(対象外: 差分に *Screen.kt が含まれないため)
```
