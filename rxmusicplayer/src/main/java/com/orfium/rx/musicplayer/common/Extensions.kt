package com.orfium.rx.musicplayer.common

import com.orfium.rx.musicplayer.RxMusicPlayer
import com.orfium.rx.musicplayer.media.Media

fun Media.isPlaying(): Boolean {
    return RxMusicPlayer.state.value is PlaybackState.Playing && (RxMusicPlayer.state.value as PlaybackState.Playing).media == this
}

fun Media.playStop() {
    if (isPlaying()) {
        RxMusicPlayer.action.onNext(Action.pause())
    } else {
        RxMusicPlayer.action.onNext(Action.start(listOf(this)))
    }
}

fun Media.addQueue() {
    RxMusicPlayer.action.onNext(Action.add(this))
}

fun Media.removeQueue() {
    RxMusicPlayer.action.onNext(Action.remove(this))
}

fun Media.seek(position: Long) {
    RxMusicPlayer.action.onNext(Action.seek(position))
}

fun Media.previous() {
    RxMusicPlayer.action.onNext(Action.previous())
}

fun Media.next() {
    RxMusicPlayer.action.onNext(Action.next())
}

fun List<Media>.isPlaying(): Boolean {
    return any { media -> media.isPlaying() }
}

fun List<Media>.playStop() {
    if (isPlaying()) {
        RxMusicPlayer.action.onNext(Action.pause())
    } else {
        RxMusicPlayer.action.onNext(Action.start(this))
    }
}

fun List<Media>.previous() {
    RxMusicPlayer.action.onNext(Action.previous())
}

fun List<Media>.next() {
    RxMusicPlayer.action.onNext(Action.next())
}
