package com.orfium.rx.musicplayer.common

import com.orfium.rx.musicplayer.media.Media

sealed class Action {

    data class Start(var media: List<Media>, var index: Int) : Action()
    data class Add(var media: Media) : Action()
    data class Remove(var media: Media) : Action()
    data class Seek(val position: Long) : Action()
    object Previous : Action()
    object Pause : Action()
    object Resume: Action()
    object Next : Action()
    object Stop : Action()

    companion object {
        fun start(media: List<Media>, index: Int = 0) : Action = Start(media, index)

        fun add(media: Media) : Action = Add(media)

        fun remove(media: Media) : Action = Remove(media)

        fun seek(position: Long) : Action = Seek(position)

        fun previous() : Action = Previous

        fun pause() : Action = Pause

        fun resume() : Action = Resume

        fun next() : Action = Next

        fun stop() : Action = Stop
    }

}