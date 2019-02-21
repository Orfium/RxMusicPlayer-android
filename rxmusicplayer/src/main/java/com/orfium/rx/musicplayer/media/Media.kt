package com.orfium.rx.musicplayer.media

open class Media(
    val id: Int = 0,
    val image: String? = null,
    val title: String? = null,
    val artist: String? = null,
    val streamUrl: String? = null,
    val duration: Int? = null
){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Media

        if (id != other.id) return false
        if (streamUrl != other.streamUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (streamUrl?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Media(id=$id, image=$image, title=$title, artist=$artist, streamUrl=$streamUrl)"
    }

}