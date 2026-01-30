# Photo Album Sample App Spec (Compose)

## Libraries
- Jetpack Compose + Compose Material (Material2)
- Navigation Compose = 2.8.3（Type-safe navigationを使う）
- DI: Koin（koin-androidx-compose の koinViewModel()）
- Image: Coil（表示UIサイズを必ず指定）
- Grid: LazyVerticalGrid（Adaptive）
- Icons: Material Icons（SVG -> VectorDrawable）

## Album definition (Screen1)
- Album = MediaStore BUCKET_ID / BUCKET_DISPLAY_NAME（フォルダ相当）
- Album list item shows:
  - album name (BUCKET_DISPLAY_NAME)
  - image count
  - cover thumbnail = 最新1枚のUri（最新 = DATE_TAKEN優先、なければDATE_ADDED等でフォールバック）
- Album list sorting: coverの日時 desc（最近のアルバムが上）

## Photos grid (Screen2)
- In selected album, show images in LazyVerticalGrid (Adaptive)
- Thumbnail:
  - square
  - centerCrop（サムネと言われたらcenterCrop）
  - CoilはUIサイズ指定
- Sorting: 撮影日時 desc（DATE_TAKEN desc / fallbackあり）
- Tap thumbnail -> Viewer (Screen3) with initial index

## Viewer (Screen3)
- ViewerRouteとして独立（Scaffold共有なし）
- Background: 真っ黒（画像以外は完全に黒）
- Fullscreen immersive: システムバー非表示
- UI: TopBarなし
- Image scale: 短辺FIT（端末画面の短辺に合わせる）
  - portrait: ContentScale.FillWidth
  - landscape: ContentScale.FillHeight
  - alignment: center, background: black
- Close: Xボタン + Backキー/BackジェスチャでScreen2へ戻る
- Pager: HorizontalPager
- 初期ページ = タップしたサムネの位置

## Permission model (IMPORTANT)
- API 33+ : READ_MEDIA_IMAGES
- API 32以下: READ_EXTERNAL_STORAGE（manifestは maxSdkVersion=32 推奨）
- Android 14+（API 34+）: 「選択した写真のみ許可」(partial access) を想定
  - 権限があっても0件になり得る（仕様）
- Sampleなので「初期化後に一発だけ」権限リクエストすれば良い
  - 設定画面への導線や管理画面などは不要

## Empty / Error handling
Empty画面で表示を出し分ける:
1) 権限なし: 説明 + 許可ボタン
2) 権限ありだが0件: “見える写真がありません（選択した写真のみ許可の可能性があります）”
3) クエリエラー: 例外メッセージ表示

## Navigation / UI structure (Screen1 & Screen2)
- Screen1とScreen2は同じTopBar/BottomBarを共有（Top/Bottomはアニメーションさせない）
- ただし Screen1 <-> Screen2 のコンテンツ切替は「Fade + 水平スライド（slide+fade）」で実現する
- Back handling:
  - Screen2でBackキー/Backジェスチャ -> Screen1へ戻る（BackStack必須）

### Implementation notes (important)
- 「アルバム+グリッド統合画面（Scaffold共有）」を作り、その中で内部Nav（NavigationCompose）を使って Screen1/Screen2 を管理してよい
- 親側のNavとの競合はバックキー制御で回避する（ex. Grid表示次のバックキーでScreen1へ戻り、アプリ終了にならないようにする）
- もし内部Navが不都合なら、Pagerや自前アニメ＋BackHandlerで同等の挙動を実現する
