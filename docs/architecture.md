# Architecture

## 目的
本プロジェクトのシステム構造、構成要素間の関係、およびそれぞれの責務を定義します。具体的な仕様については `docs/spec.md` を参照してください。

## 非ゴール
- マルチモジュール化（単一アプリモジュールで構成）
- 複雑なキャッシュ層の構築（MediaStoreを直接ソースとする）
- 汎用的なメディアプレイヤー機能の実装

## モジュール/ディレクトリ構成
- `app/`
  - `src/main/java/com/example/photogallerysample/`
    - `ui/`: Composeによる画面構成、テーマ、コンポーネント
    - `data/`: MediaStoreとのやり取り、リポジトリ層
    - `di/`: Koinによる依存注入定義
    - `model/`: UI用、ドメイン用データモデル

## 画面/Route構成
- **GalleryRoute**: アルバム一覧（Screen1）および写真グリッド（Screen2）を内包。Scaffoldを共有し、内部ナビゲーションで切り替える。
- **ViewerRoute**: 写真詳細表示（Screen3）。独立したルートとして構成し、システムUI制御を行う。

## データフロー
`MediaStore -> Repository -> ViewModel(UiState) -> UI`

## 主要コンポーネント責務
- **Repository**: MediaStoreへのクエリ実行、生データをドメイン/UI用モデルへ変換。
- **ViewModel**: UI状態（UiState）の管理、Repositoryからのデータ取得、UIイベントの処理。
- **UI (Compose)**: UiStateに基づいた画面の描画、ユーザー入力のViewModelへの通知。
