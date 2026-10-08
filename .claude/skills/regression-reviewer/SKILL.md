---
name: regression-reviewer
description: 過去に発生したデグレの再発防止チェックリスト（.claude/rules/regression-patterns.md）に、現在の差分（git diff）が抵触していないかレビューする。
disable-model-invocation: true
allowed-tools: Read, Glob, Bash
---

# デグレ再発防止レビュー

`.claude/rules/regression-patterns.md` に蓄積された、過去に ramen-note で発生したデグレの
再発防止チェックリストに基づき、現在の差分が同じ問題を再発させていないかを静的にレビューする。
すべての出力は日本語で行う。

新たなデグレが発生した場合は `.claude/rules/regression-patterns.md` に `CHECK-N` セクションを
追記して蓄積していく（本ファイル自体の変更は不要）。

---

## ステップ1: 差分の取得

**引数に `--staged` が指定されている場合**（`.githooks/pre-commit` からの自動呼び出しを想定）は、
以下の判定を行わず `git diff --cached` のみを無条件にレビュー対象にする。これから commit される
内容だけを見るべきで、commit に含まれない未ステージの変更まで対象にすると誤ってブロックしうるため。

```bash
git diff --cached              # --staged 指定時は常にこれ
```

**引数がない場合**（対話的な呼び出し）は、以下の判定を行う。

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

## ステップ2: 対象チェックの判定

`.claude/rules/regression-patterns.md` を読み込み、そこに定義されている各 `CHECK-N` セクションの
「チェック対象ファイル」欄と、差分に含まれるファイルパス・内容を照合する。マッチしない `CHECK-N` は
「対象外」としてスキップする。

全 `CHECK-N` が対象外であれば、ステップ3・4は省略し「対象ファイルなし」として PASS 扱いで終了する
（`--staged` 時は `REGRESSION_REVIEW_RESULT: PASS` を出力する）。

---

## ステップ3: 該当チェックの実施

対象と判定した `CHECK-N` について、`.claude/rules/regression-patterns.md` の該当セクションに記載
された確認項目（`N-A`、`N-B`、...）に沿って差分の内容を確認する。各項目を
✅ PASS / ❌ FAIL / ⚠️ 要確認 で判定する。

---

## ステップ4: 結果の報告

チェックごとに見出しを立て、対象外・PASS・FAIL・要確認を報告する。FAIL・要確認には具体的な
問題箇所（ファイルパス・該当箇所）と修正案を示す。**このスキルでは修正の実施は行わない**
（提示のみ。実際の編集はユーザーの承認を得てから別途行う）。

**引数に `--staged` が指定されている場合**は、上記の報告に加えて**必ず最後の1行**に以下の
いずれかを出力する（`.githooks/pre-commit` がこの行を機械的に判定するため、文言・大文字小文字を
変えないこと）。要確認（⚠️）は commit を止めるほどではないため PASS 扱いとし、FAIL が1件でも
あれば FAIL とする。

```
REGRESSION_REVIEW_RESULT: PASS
REGRESSION_REVIEW_RESULT: FAIL
```

出力フォーマット例:

```
## デグレレビュー結果

### CHECK-1: HistoryScreen 自動スクロール機能
(対象外: 差分に HistoryScreen.kt が含まれないため)

### CHECK-2: RunStatus.Success 完了コールバックの LaunchedEffect ラップ
| 項目 | 判定 | 詳細 |
|------|------|------|
| 2-A: LaunchedEffect でのラップ | ❌ FAIL | AddAreaScreen.kt: onCompleted() がコンポーザブル本体で直接呼ばれている |
| 2-B: キーの妥当性 | - | (2-A が FAIL のため評価不能) |

修正案: is RunStatus.Success -> { onCompleted() } を
       is RunStatus.Success -> { LaunchedEffect(addStatus) { onCompleted() } } に変更する

REGRESSION_REVIEW_RESULT: FAIL
```
