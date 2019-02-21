package com.orfium.rx.musicplayer.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import com.orfium.rx.musicplayer.R

@RequiresApi(api = Build.VERSION_CODES.O)
internal class NotificationChannels(base: Context) : ContextWrapper(base) {

    val primaryChannel: String = getString(R.string.app_name)

    private var manager: NotificationManager? = null

    init {
        val channelPrimary = NotificationChannel(primaryChannel, primaryChannel, NotificationManager.IMPORTANCE_DEFAULT)
        channelPrimary.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channelPrimary.setSound(null, null)
        channelPrimary.enableVibration(false)
        getManager().createNotificationChannel(channelPrimary)
    }

    private fun getManager(): NotificationManager {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager!!
    }

}