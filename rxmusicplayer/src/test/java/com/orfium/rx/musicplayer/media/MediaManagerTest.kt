package com.orfium.rx.musicplayer.media

import com.orfium.rx.musicplayer.RxMusicPlayer
import com.orfium.rx.musicplayer.common.Action
import com.orfium.rx.musicplayer.common.PlaybackState
import com.orfium.rx.musicplayer.playback.Playback
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MediaManagerTest {

    @Mock
    internal lateinit var queue: MediaQueue
    @Mock
    internal lateinit var playerCallback: Playback.PlayerCallback
    @Mock
    internal lateinit var serviceCallback: Playback.ServiceCallback

    @InjectMocks
    lateinit var mediaManager: MediaManager

    @Before
    fun setUp(){
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline()}
    }

    @Test
    fun testOnBufferThenStateChangedBuffering() {
        val media = createMedia()
        val state = PlaybackState.buffering(media)
        val position = 0L

        `when`(queue.current).thenReturn(media)
        `when`(playerCallback.position).thenReturn(position)

        mediaManager.onBuffer()

        verify(serviceCallback).onPlaybackStateChanged(state, position)
    }

    @Test
    fun testOnPlayThenStateChangedPlaying() {
        val media = createMedia()
        val state = PlaybackState.playing(media)
        val position = 100L

        `when`(queue.current).thenReturn(media)
        `when`(playerCallback.position).thenReturn(position)

        mediaManager.onPlay()

        verify(serviceCallback).onPlaybackStateChanged(state, position)
    }

    @Test
    fun testOnPauseThenStateChangedPaused() {
        val media = createMedia()
        val state = PlaybackState.paused(media)
        val position = 1000L

        `when`(queue.current).thenReturn(media)
        `when`(playerCallback.position).thenReturn(position)

        mediaManager.onPause()

        verify(serviceCallback).onPlaybackStateChanged(state, position)
    }

    @Test
    fun testOnCompletionThenPlay() {
        val media = createMedia()
        val state = PlaybackState.playing(media)
        val position = 0L

        `when`(queue.hasNext()).thenReturn(true)
        `when`(queue.next).thenReturn(media)
        `when`(playerCallback.position).thenReturn(position)

        mediaManager.onCompletion()

        verify(playerCallback).play(media)
        verify(serviceCallback).onPlaybackMediaChanged(media)
        verify(serviceCallback).onPlaybackStateChanged(state, position)
    }

    @Test
    fun testOnCompletionThenStateChangedCompleted() {
        val media = createMedia()
        val state = PlaybackState.completed(media)
        val position = 5000L

        `when`(queue.current).thenReturn(media)
        `when`(playerCallback.position).thenReturn(position)

        mediaManager.onCompletion()

        verify(serviceCallback).onPlaybackStateChanged(state, position)
    }

    @Test
    fun testOnIdleThenPlaybackStateChangedIdle() {
        val state = PlaybackState.idle()
        val position = 0L

        mediaManager.onIdle()

        verify(serviceCallback).onPlaybackStateChanged(state, position)
    }

    @Test
    fun testSubscribeQueueWithActionPlayThenSetQueueAndPlay() {
        val media = createMedia()
        val list = listOf(media)
        val index = 0
        val state = PlaybackState.playing(media)
        val position = 0L
        val action = Action.start(list, index)

        `when`(queue.current).thenReturn(media)
        mediaManager.subscribeQueue()
        RxMusicPlayer.action.onNext(action)

        assertTrue(action is Action.Start)
        verify(queue).setQueue(list, index)
        verify(playerCallback).play(media)
        verify(serviceCallback).onPlaybackMediaChanged(media)
        verify(serviceCallback).onPlaybackStateChanged(state, position)
    }

    @Test
    fun testSubscribeQueueWithActionAddThenAddQueue() {
        val media = createMedia()
        val action = Action.add(media)

        mediaManager.subscribeQueue()
        RxMusicPlayer.action.onNext(action)

        assertTrue(action is Action.Add)
        verify(queue).addQueue(media)
    }

    @Test
    fun testSubscribeQueueWithActionRemoveThenRemoveQueue() {
        val media = createMedia()
        val action = Action.remove(media)

        mediaManager.subscribeQueue()
        RxMusicPlayer.action.onNext(action)

        assertTrue(action is Action.Remove)
        verify(queue).removeQueue(media)
    }

    @Test
    fun testSubscribeQueueWithActionSeekThenSeekTo() {
        val position = 100L
        val action = Action.seek(position)

        mediaManager.subscribeQueue()
        RxMusicPlayer.action.onNext(action)

        assertTrue(action is Action.Seek)
        verify(playerCallback).seekTo(position)
    }

    @Test
    fun testSubscribeQueueWithActionPreviousThenInvalidateCurrentAndPlayPrevious() {
        val current = createMedia(id = 1)
        val previous = createMedia(id = 2)
        val position = 0L
        val action = Action.previous()
        val state = PlaybackState.playing(previous)

        `when`(playerCallback.position).thenReturn(position)
        `when`(queue.current).thenReturn(current)
        `when`(queue.previous).thenReturn(previous)

        mediaManager.subscribeQueue()
        RxMusicPlayer.action.onNext(action)

        assertTrue(action is Action.Previous)
        verify(playerCallback).invalidateCurrent()
        verify(playerCallback).play(previous)
        verify(serviceCallback).onPlaybackMediaChanged(previous)
        verify(serviceCallback).onPlaybackStateChanged(state, position)
    }

    @Test
    fun testSubscribeQueueWithActionPreviousThenInvalidateCurrentAndPlayCurrent() {
        val current = createMedia(id = 1)
        val position = 10000L
        val action = Action.previous()
        val state = PlaybackState.playing(current)

        `when`(playerCallback.position).thenReturn(position)
        `when`(queue.current).thenReturn(current)

        mediaManager.subscribeQueue()
        RxMusicPlayer.action.onNext(action)

        assertTrue(action is Action.Previous)
        verify(playerCallback).invalidateCurrent()
        verify(playerCallback).play(current)
        verify(serviceCallback).onPlaybackMediaChanged(current)
        verify(serviceCallback).onPlaybackStateChanged(state, position)
    }

    @Test
    fun testSubscribeQueueWithActionPauseThenPause() {
        val action = Action.pause()

        mediaManager.subscribeQueue()
        RxMusicPlayer.action.onNext(action)

        assertTrue(action is Action.Pause)
        verify(playerCallback).pause()
    }

    @Test
    fun testSubscribeQueueWithActionResumeThenPlay() {
        val media = createMedia()
        val position = 100L
        val action = Action.resume()
        val state = PlaybackState.playing(media)

        `when`(queue.current).thenReturn(media)
        `when`(playerCallback.position).thenReturn(position)

        mediaManager.subscribeQueue()
        RxMusicPlayer.action.onNext(action)

        assertTrue(action is Action.Resume)
        verify(playerCallback).play(media)
        verify(serviceCallback).onPlaybackMediaChanged(media)
        verify(serviceCallback).onPlaybackStateChanged(state, position)
    }

    @Test
    fun testSubscribeQueueWithActionNextThenPlayNext() {
        val media = createMedia()
        val position = 100L
        val action = Action.next()
        val state = PlaybackState.playing(media)

        `when`(queue.next).thenReturn(media)
        `when`(playerCallback.position).thenReturn(position)

        mediaManager.subscribeQueue()
        RxMusicPlayer.action.onNext(action)

        assertTrue(action is Action.Next)
        verify(playerCallback).play(media)
        verify(serviceCallback).onPlaybackMediaChanged(media)
        verify(serviceCallback).onPlaybackStateChanged(state, position)
    }

    @Test
    fun testUnSubscribeQueue() {
        val media = createMedia()
        val action = Action.resume()

        mediaManager.unSubscribeQueue()
        RxMusicPlayer.action.onNext(action)

        assertTrue(action is Action.Resume)
        verify(playerCallback, times(0)).play(media)
    }

    private fun createMedia(id: Int = 1, streamUrl: String = java.util.UUID.randomUUID().toString()): Media {
        return Media(id = id, streamUrl = streamUrl)
    }
}