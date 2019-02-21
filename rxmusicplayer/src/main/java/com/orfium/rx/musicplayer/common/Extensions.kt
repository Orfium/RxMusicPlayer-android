package com.orfium.rx.musicplayer.common

import com.orfium.rx.musicplayer.RxPlayer
import com.orfium.rx.musicplayer.media.Media

fun Media.isPlaying(): Boolean {
    return RxPlayer.state.value is PlaybackState.Playing && (RxPlayer.state.value as PlaybackState.Playing).media == this
}

fun Media.playStop() {
    if (isPlaying()) {
        RxPlayer.action.onNext(Action.pause())
    } else {
        RxPlayer.action.onNext(Action.start(listOf(this)))
    }
}

fun Media.addQueue() {
    RxPlayer.action.onNext(Action.add(this))
}

fun Media.removeQueue() {
    RxPlayer.action.onNext(Action.remove(this))
}

fun Media.seek(position: Long) {
    RxPlayer.action.onNext(Action.seek(position))
}

fun Media.previous() {
    RxPlayer.action.onNext(Action.previous())
}

fun Media.next() {
    RxPlayer.action.onNext(Action.next())
}

fun List<Media>.isPlaying(): Boolean {
    return any { media -> media.isPlaying() }
}

fun List<Media>.playStop() {
    if (isPlaying()) {
        RxPlayer.action.onNext(Action.pause())
    } else {
        RxPlayer.action.onNext(Action.start(this))
    }
}

fun List<Media>.previous() {
    RxPlayer.action.onNext(Action.previous())
}

fun List<Media>.next() {
    RxPlayer.action.onNext(Action.next())
}
