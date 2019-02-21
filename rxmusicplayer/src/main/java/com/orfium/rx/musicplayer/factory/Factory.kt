package com.orfium.rx.musicplayer.factory

import android.app.Service
import android.content.Context
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationManagerCompat
import com.orfium.rx.musicplayer.R
import com.orfium.rx.musicplayer.media.MediaManager
import com.orfium.rx.musicplayer.media.MediaQueue
import com.orfium.rx.musicplayer.notification.NotificationManager
import com.orfium.rx.musicplayer.playback.Playback
import com.orfium.rx.musicplayer.playback.PlaybackImpl

internal object Factory {

    fun createMediaManager(serviceCallback: Playback.ServiceCallback): MediaManager {
        val mediaQueue = createQueue()
        val playerCallback = createPlayback(serviceCallback as Context)
        return MediaManager(
            mediaQueue,
            playerCallback,
            serviceCallback
        ).apply { playerCallback.setCallback(this) }
    }

    fun createMediaNotification(service: Service, token: MediaSessionCompat.Token): NotificationManager {
        val notificationManager = createNotificationManager(service)
        return NotificationManager(
            service,
            token,
            notificationManager
        )
    }

    private fun createQueue(): MediaQueue = MediaQueue()

    private fun createPlayback(context: Context): Playback.PlayerCallback {
        return PlaybackImpl(
            context,
            createAudioManager(context),
            createWifiManager(context)
        )
    }

    private fun createAudioManager(context: Context): AudioManager {
        return context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private fun createWifiManager(context: Context): WifiManager.WifiLock {
        return (context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager)
            .createWifiLock(WifiManager.WIFI_MODE_FULL, context.getString(R.string.app_name))
    }

    private fun createNotificationManager(context: Context): NotificationManagerCompat {
        return NotificationManagerCompat.from(context)
    }

}