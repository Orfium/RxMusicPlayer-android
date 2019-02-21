package com.orfium.rx.musicplayer.common

import com.orfium.rx.musicplayer.media.Media

data class QueueData(

    val media: List<Media>,
    val index: Int

)