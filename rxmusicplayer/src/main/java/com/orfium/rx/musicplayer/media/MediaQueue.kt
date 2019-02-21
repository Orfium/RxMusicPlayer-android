package com.orfium.rx.musicplayer.media

internal class MediaQueue {

    private val queue: MutableList<Media> = mutableListOf()
    private var index: Int = 0

    val list: List<Media>
        get() = queue

    val currentIndex: Int
        get() = index

    val current: Media?
        get() = if (!isEmpty()) queue[index] else null

    val next: Media?
        get() = if (hasNext()) queue[++index] else current

    val previous: Media?
        get() = if (hasPrevious()) queue[--index] else current

    fun setQueue(media: List<Media>, newIndex: Int = 0) {
        queue.clear()
        queue.addAll(media)
        index = newIndex
    }

    fun addQueue(media: Media) {
        queue.add(media)
    }

    fun removeQueue(media: Media) {
        if (!hasNext()) index -= 1
        queue.remove(media)
    }

    fun hasNext(): Boolean {
        return !isEmpty() && queue.size > index + 1
    }

    private fun isEmpty(): Boolean {
        return queue.isEmpty()
    }

    private fun hasPrevious(): Boolean {
        return !isEmpty() && index - 1 >= 0
    }
}