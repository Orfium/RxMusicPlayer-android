package com.orfium.rx.musicplayer.playback

import com.orfium.rx.musicplayer.common.PlaybackState
import com.orfium.rx.musicplayer.media.Media

internal interface Playback {

    /* The base interface for any playback implementation */
    interface PlayerCallback {

        val isPlaying: Boolean

        val position: Long

        val duration: Long

        fun play(media: Media?)

        fun pause()

        fun complete()

        fun stop()

        fun seekTo(position: Long)

        fun setCallback(callback: ManagerCallback)

        fun invalidateCurrent()

    }

    /* This interface is used to notify MediaPlaybackManager about changes in the player*/
    interface ManagerCallback {

        fun onBuffer()

        fun onPlay()

        fun onPause()

        fun onCompletion()

        fun onIdle()

        fun onError()

    }

    /* This interface is used to notify Foreground Service about changes in the player's state*/
    interface ServiceCallback {

        fun onPlaybackStateChanged(state: PlaybackState, position: Long)

        fun onPlaybackMediaChanged(media: Media?)

    }

}