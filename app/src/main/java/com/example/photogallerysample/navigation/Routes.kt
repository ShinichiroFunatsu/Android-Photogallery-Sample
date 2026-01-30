package com.example.photogallerysample.navigation

import kotlinx.serialization.Serializable

/**
 * アプリ内の型安全なルート定義。
 * 階層構造はネストされた型を使用して明示的に定義されています。
 */
sealed interface Routes {

    /**
     * ギャラリー機能の親ルート。
     * アルバム一覧と写真グリッドの内部ルートを含みます。
     */
    @Serializable
    object Gallery : Routes {

        /**
         * アルバム一覧を表示するルート。
         */
        @Serializable
        object AlbumList

        /**
         * 特定のアルバム内の写真を表示するルート。
         * @param bucketId 表示するアルバム（バケット）のID。
         */
        @Serializable
        data class PhotosGrid(
            val bucketId: String
        )
    }

    /**
     * フルスクリーンの写真ビューアーを表示する独立したルート。
     * @param bucketId 閲覧中のアルバムのID。
     * @param initialIndex 最初に表示する写真のインデックス。
     */
    @Serializable
    data class Viewer(
        val bucketId: String,
        val initialIndex: Int
    ) : Routes
}
