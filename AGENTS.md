# AGENTS.md

このリポジトリで作業するコーディングエージェント向けの運用ルールです。  
仕様の唯一の正は `docs/spec.md` です。

## 関連ドキュメント
- [docs/spec.md](docs/spec.md): 仕様（What）
- [docs/decisions.md](docs/decisions.md): 重要な判断理由（Why）
- [docs/architecture.md](docs/architecture.md): 構造/責務（Howの骨格）

---

## 1. 仕様の扱い（必須）

- 作業前に `docs/spec.md` を読む。
- `docs/spec.md` に書かれていない機能・挙動・依存関係を実装しない。
- 仕様変更が必要な場合は、実装より先に `docs/spec.md` を更新する。

---

## 2. タスク境界（必須）

- タスクで指定された目的・範囲に変更を限定する。
- 指定されていないファイルや機能を勝手に変更しない。
- 仕様に影響しない整形・命名変更・ディレクトリ再編成を行わない。
- 「ついでのリファクタ」「ついでのライブラリ追加」を行わない。
- 指定外の変更が必要になった場合は、実装前に以下を提示する（提示なしで実装しない）:
    - 必要な理由
    - 変更するファイル一覧
    - 代替案（指定外の変更を避ける案がある場合）

---

## 3. コードスタイル（必須）

- コードスタイルはAOSPに準拠する。
- 自動フォーマットの実行は要求しない（エージェント実行環境で保証できないため）。
- タスク完了時に、変更したKotlinファイルについて次を必ず満たす:
    - 未使用importを残さない
    - importは辞書順でソートする
    - ワイルドカードimportを追加しない

---

## 4. 技術スタック（変更禁止）

`docs/spec.md` に基づき、以下は勝手に変更しない。

- Compose Material (Material2)
- Navigation Compose 2.8.3（type-safe navigation）
- Koin（`koin-androidx-compose` の `koinViewModel()`）
- Coil（表示UIサイズ指定必須）
- LazyVerticalGrid(Adaptive)
- Icons: Material Icons（SVG -> VectorDrawable）
- minSdk=26, targetSdk=36

---

## 5. 配置ルール（必須）

既存の構成を優先し、勝手に再編成しない。新規追加が必要な場合は責務で分ける。

- `navigation/` : route定義、NavHost、遷移ロジック
- `ui/` : 画面Composable、共通UI
- `viewmodel/` : ViewModel、UiState（StateFlow）
- `data/` : Repository、データ取得
- `di/` : Koin module定義

---

## 6. 依存関係追加（必須）

- `docs/spec.md` に無い依存関係は追加しない。
- 追加が不可避な場合は、先に `docs/decisions.md`（無ければ新規作成）に「理由・代替案・追加内容」を短く記録し、その後に追加する。

---

## 7. ビルド・検証（必須）

### 7.1 作業中（.kt を書き換えた直後）
- `./gradlew :app:compileDebugKotlin`
- エラーが出たら修正して同じコマンドを再実行する。

### 7.2 次の変更を行った直後
- AndroidManifest.xml を変更:
    - `./gradlew :app:processDebugMainManifest`
- res/ を変更:
    - `./gradlew :app:processDebugResources`
- Gradle / 依存関係 / plugin / version catalog を変更:
    - `./gradlew :app:assembleDebug`

### 7.3 タスク完了時（必須）
- 変更したKotlinファイルのimportを整理（AGENTS.md 3章の条件を満たす）
- `./gradlew :app:assembleDebug`
- `./gradlew :app:lintDebug`
- `./gradlew :app:test`（存在する範囲で実行）
- 成功するまで修正する。

### 7.4 push前（必須）
- 7.3 を再実行して成功させる（タスク完了後に修正した場合は省略禁止）。

### 7.5 タスク名が存在しない場合
- `./gradlew :app:tasks --all` で同等タスクを探す。
- `processDebugMainManifest` / `processDebugResources` が無い場合は `./gradlew :app:assembleDebug` を実行する。
- `lintDebug` が無い場合は `./gradlew :app:lint` を実行する。

### 7.6 完了報告
- 実行したコマンドをそのまま列挙する（省略しない）。

---

## 8. 追加してよいドキュメント（限定）

仕様は `docs/spec.md` に集約する。追加してよいのは次のみ。

- `docs/decisions.md` : 判断理由・代替案・結論（短く）
- `docs/architecture.md` : 画面/route構造、責務分割、データフロー
- `docs/dev-setup.md` : 開発環境セットアップとコマンド

## 9. 言語（必須）

- コード内コメントは日本語で記述する。
- エージェントの作業報告/ユーザーへの説明は日本語で記述する。
- それ以外（識別子、ファイル名、ブランチ名、コミットメッセージ）は英語で記述する。
- 既存ファイルの言語は変更しない（翻訳目的の修正をしない）。
