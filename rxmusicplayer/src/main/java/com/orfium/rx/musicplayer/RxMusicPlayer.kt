package com.orfium.rx.musicplayer

import android.content.Context
import android.content.Intent
import com.orfium.rx.musicplayer.common.Action
import com.orfium.rx.musicplayer.common.PlaybackState
import com.orfium.rx.musicplayer.common.QueueData
import com.orfium.rx.musicplayer.media.MediaService
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

object RxMusicPlayer {

    private val playbackStateSubject = BehaviorSubject.create<PlaybackState>()
    private val queueSubject = BehaviorSubject.create<QueueData>()
    private val actionSubject = PublishSubject.create<Action>()
    private val playbackPositionSubject = PublishSubject.create<Long>()

    @JvmStatic
    fun start(context: Context) {
        context.startService(Intent(context, MediaService::class.java))
    }

    @JvmStatic
    fun start(context: Context, notificationIntent: Intent, notificationIconRes: Int) {
        val intent = Intent(context, MediaService::class.java)
        intent.putExtra(MediaService.EXTRA_NOTIFICATION_INTENT, notificationIntent)
        intent.putExtra(MediaService.EXTRA_NOTIFICATION_ICON_RES, notificationIconRes)
        context.startService(intent)
    }

    @JvmStatic
    val state: BehaviorSubject<PlaybackState>
        get() = playbackStateSubject

    @JvmStatic
    val queue: BehaviorSubject<QueueData>
        get() = queueSubject

    @JvmStatic
    val action: PublishSubject<Action>
        get() = actionSubject

    @JvmStatic
    val position: PublishSubject<Long>
        get() = playbackPositionSubject

}