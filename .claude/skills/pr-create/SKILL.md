---
name: pr-create
description: 現在のブランチから Pull Request を日本語で作成する。引数でベースブランチを指定可能
disable-model-invocation: true
allowed-tools: Bash
---

# Pull Request 作成タスク

ベースブランチは `$ARGUMENTS` が指定されていればそれを、なければ `main` を使用してください。

## ステップ 1: 現在の状態を把握する

以下を並列で実行して状態を把握する：

```bash
git status
git log main..HEAD --oneline
git diff main...HEAD --stat
git branch -vv
```

## ステップ 2: 変更内容を分析する

コミット履歴と差分から以下を把握する：

- 変更の目的・背景
- 具体的な変更ファイルと内容
- テスト方法・確認事項

## ステップ 3: リモートに push する（必要な場合）

現在のブランチがリモートに存在しない、または push が必要な場合：

```bash
git push -u origin <ブランチ名>
```

## ステップ 4: PR を作成する

以下のフォーマットで日本語の PR を `gh pr create` で作成する：

**タイトル**: 変更内容の簡潔な要約（50文字以内、体言止め）

**本文フォーマット**:
```
### 概要
- 変更の目的と背景を箇条書きで記述

### 変更内容
- 具体的な変更点をファイル・機能単位で記述

### テスト
- テスト方法と確認事項を記述
```

実行コマンド：
```bash
gh pr create \
  --base <ベースブランチ> \
  --title "<タイトル>" \
  --body "<本文>"
```

作成後、PR の URL を表示する。
