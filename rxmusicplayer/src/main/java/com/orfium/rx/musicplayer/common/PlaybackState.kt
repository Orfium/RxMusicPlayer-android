package com.orfium.rx.musicplayer.common

import com.orfium.rx.musicplayer.media.Media

sealed class PlaybackState {

    object Idle : PlaybackState()
    object Stopped : PlaybackState()
    data class Buffering(var media: Media?) : PlaybackState()
    data class Playing(var media: Media?) : PlaybackState()
    data class Paused(var media: Media?) : PlaybackState()
    data class Completed(var media: Media?) : PlaybackState()

    companion object {
        fun idle(): PlaybackState =
            Idle

        /**
         * When this method gets called, MediaService gets destroyed.
         * [com.orfium.rx.musicplayer.RxMusicPlayer.start] method needs to be called again
         */
        fun stopped(): PlaybackState =
            Stopped

        fun buffering(media: Media?): PlaybackState =
            Buffering(media)

        fun playing(media: Media?): PlaybackState =
            Playing(media)

        fun paused(media: Media?): PlaybackState =
            Paused(media)

        fun completed(media: Media?): PlaybackState =
            Completed(media)
    }
}