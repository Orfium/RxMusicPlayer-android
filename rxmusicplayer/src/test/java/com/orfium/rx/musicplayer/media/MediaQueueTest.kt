package com.orfium.rx.musicplayer.media

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MediaQueueTest {

    private val mediaQueue = MediaQueue()

    @Test
    fun setQueueTest() {
        val media = listOf(createMedia(1), createMedia(2), createMedia(3))

        mediaQueue.setQueue(media)

        assertTrue(mediaQueue.list.isNotEmpty())
        assertEquals(media, mediaQueue.list)
    }

    @Test
    fun setQueueWithIndexTest() {
        val media = listOf(createMedia(1), createMedia(2), createMedia(3))
        val position = 2

        mediaQueue.setQueue(media, position)

        assertTrue(mediaQueue.list.isNotEmpty())
        assertEquals(media, mediaQueue.list)
        assertEquals(media[position], mediaQueue.current)
    }

    @Test
    fun addQueueTest() {
        val media = createMedia(1)

        mediaQueue.addQueue(media)
        assertTrue(mediaQueue.list.contains(media))
    }

    @Test
    fun removeQueueTest() {
        val item0 = createMedia(1)
        val item1 = createMedia(2)
        val item2 = createMedia(3)
        val media = listOf(item0, item1, item2)

        mediaQueue.setQueue(media)
        mediaQueue.removeQueue(item1)
        assertFalse(mediaQueue.list.contains(item1))
    }

    @Test
    fun removeQueueCurrentMediaTest() {
        val item0 = createMedia(1)
        val item1 = createMedia(2)
        val item2 = createMedia(3)
        val media = listOf(item0, item1, item2)
        val position = 2

        mediaQueue.setQueue(media, position)
        mediaQueue.removeQueue(item2)

        assertFalse(mediaQueue.list.contains(item2))
        assertNotEquals(item2, mediaQueue.current)
        assertEquals(item1, mediaQueue.current)
    }

    private fun createMedia(id: Int = 1, streamUrl: String = java.util.UUID.randomUUID().toString()): Media {
        return Media(id = id, streamUrl = streamUrl)
    }

}