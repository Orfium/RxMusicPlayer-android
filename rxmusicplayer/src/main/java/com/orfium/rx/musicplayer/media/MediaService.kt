package com.orfium.rx.musicplayer.media

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.session.MediaButtonReceiver
import com.orfium.rx.musicplayer.RxMusicPlayer
import com.orfium.rx.musicplayer.common.Action
import com.orfium.rx.musicplayer.common.PlaybackState
import com.orfium.rx.musicplayer.factory.Factory
import com.orfium.rx.musicplayer.notification.NotificationManager
import com.orfium.rx.musicplayer.playback.Playback

internal class MediaService: Service(), Playback.ServiceCallback {

    companion object {
        const val ACTION_PAUSE = "com.orfium.rx.musicplayer.pause"
        const val ACTION_PLAY  = "com.orfium.rx.musicplayer.start"
        const val ACTION_PREV  = "com.orfium.rx.musicplayer.previous"
        const val ACTION_NEXT  = "com.orfium.rx.musicplayer.next"
        const val ACTION_STOP  = "com.orfium.rx.musicplayer.stop"
    }

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var notificationManager: NotificationManager
    private lateinit var mediaManager: MediaManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val receiverComponentName = ComponentName(packageName, javaClass.name)
        mediaSession = MediaSessionCompat(applicationContext, javaClass.name, receiverComponentName,  null).apply {
            setCallback(MediaSessionCallback())
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        }
        notificationManager = Factory.createMediaNotification(this, mediaSession.sessionToken)
        mediaManager = Factory.createMediaManager(this).apply {
            subscribeQueue()
        }
    }

    override fun onStartCommand(startIntent: Intent?, flags: Int, startId: Int): Int {
        if (startIntent != null) {
            val action = startIntent.action
            if (action != null) {
                executeTask(action)
            }
            MediaButtonReceiver.handleIntent(mediaSession, startIntent)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        mediaSession.release()
        mediaManager.unSubscribeQueue()
        stopForeground(true)
        super.onDestroy()
    }

    override fun onPlaybackStateChanged(state: PlaybackState, position: Long) {
        setSessionState(state, position)
        notificationManager.updateState(state)
    }

    override fun onPlaybackMediaChanged(media: Media?) {
        mediaSession.setMetadata(getMetadata(media))
        notificationManager.updateMedia(media)
    }

    private fun setSessionState(state: PlaybackState, position: Long) {
        val stateCompat = PlaybackStateCompat.Builder()
            .setActions(getActions(state))
            .setState(
                when (state) {
                    is PlaybackState.Playing -> PlaybackStateCompat.STATE_PLAYING
                    is PlaybackState.Buffering -> PlaybackStateCompat.STATE_BUFFERING
                    is PlaybackState.Paused -> PlaybackStateCompat.STATE_PAUSED
                    is PlaybackState.Completed -> PlaybackStateCompat.STATE_PAUSED
                    is PlaybackState.Stopped -> PlaybackStateCompat.STATE_STOPPED
                    else -> PlaybackStateCompat.STATE_NONE
                }, position, 1.0f, SystemClock.elapsedRealtime()
            )
            .build()

        mediaSession.isActive = state is PlaybackState.Playing || state is PlaybackState.Buffering
        mediaSession.setPlaybackState(stateCompat)
    }

    private fun getActions(state: PlaybackState): Long {
        var actions = PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
        if (state is PlaybackState.Playing || state is PlaybackState.Buffering) {
            actions = actions or PlaybackStateCompat.ACTION_PAUSE
        } else {
            actions = actions or PlaybackStateCompat.ACTION_PLAY
        }
        return actions
    }

    private fun getMetadata(media: Media?): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, media?.image)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, media?.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, media?.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, media?.streamUrl)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, media?.id?.toString())
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, media?.duration?.toLong() ?: 0)
            .build()
    }

    private fun executeTask(action: String) {
        when (action) {
            ACTION_PLAY  -> RxMusicPlayer.action.onNext(Action.resume())
            ACTION_PREV  -> RxMusicPlayer.action.onNext(Action.previous())
            ACTION_NEXT  -> RxMusicPlayer.action.onNext(Action.next())
            ACTION_PAUSE -> RxMusicPlayer.action.onNext(Action.pause())
            ACTION_STOP  -> RxMusicPlayer.action.onNext(Action.stop())
        }
    }

    private inner class MediaSessionCallback : MediaSessionCompat.Callback() {

        override fun onPlay() {
            RxMusicPlayer.action.onNext(Action.resume())
        }

        override fun onSkipToNext() {
            RxMusicPlayer.action.onNext(Action.next())
        }

        override fun onSkipToPrevious() {
            RxMusicPlayer.action.onNext(Action.previous())
        }

        override fun onPause() {
            RxMusicPlayer.action.onNext(Action.pause())
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            // TODO
        }

        override fun onSetShuffleMode(@PlaybackStateCompat.ShuffleMode shuffleMode: Int) {
            // TODO
        }

        override fun onStop() {
            RxMusicPlayer.action.onNext(Action.stop())
        }

        override fun onSeekTo(pos: Long) {
            RxMusicPlayer.action.onNext(Action.seek(pos))
        }
    }

}