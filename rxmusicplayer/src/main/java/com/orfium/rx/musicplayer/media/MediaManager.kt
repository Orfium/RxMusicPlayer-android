package com.orfium.rx.musicplayer.media

import com.orfium.rx.musicplayer.RxMusicPlayer
import com.orfium.rx.musicplayer.common.PlaybackState
import com.orfium.rx.musicplayer.common.Action
import com.orfium.rx.musicplayer.common.QueueData
import com.orfium.rx.musicplayer.playback.Playback
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class MediaManager internal constructor(
    private val queue: MediaQueue,
    private val playerCallback: Playback.PlayerCallback,
    private val serviceCallback: Playback.ServiceCallback
) : Playback.ManagerCallback {

    private val compositeDisposable = CompositeDisposable()

    override fun onBuffer() {
        updatePlaybackState(PlaybackState.buffering(queue.current), playerCallback.position)
    }

    override fun onPlay() {
        updatePlaybackState(PlaybackState.playing(queue.current), playerCallback.position)
    }

    override fun onPause() {
        updatePlaybackState(PlaybackState.paused(queue.current), playerCallback.position)
    }

    override fun onCompletion() {
        if (queue.hasNext()) {
            play(queue.next)
        } else {
            updatePlaybackState(PlaybackState.completed(queue.current), playerCallback.position)
        }
    }

    override fun onIdle() {
        updatePlaybackState(PlaybackState.idle(), 0)
    }

    override fun onError() {

    }

    fun subscribeQueue() {
        val state = RxMusicPlayer.action
            .subscribe { action ->
                when (action) {
                    is Action.Start -> handlePlayRequest(action.media, action.index)
                    is Action.Add -> handleAddRequest(action.media)
                    is Action.Remove -> handleRemoveRequest(action.media)
                    is Action.Seek -> handleSeekRequest(action.position)
                    is Action.Previous -> handlePrevRequest()
                    is Action.Pause -> handlePauseRequest()
                    is Action.Resume -> handleResumeRequest()
                    is Action.Next -> handleNextRequest()
                    is Action.Stop -> handleStopRequest()
                }
            }
        val position = Observable.interval(500, TimeUnit.MILLISECONDS)
            .timeInterval()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ RxMusicPlayer.position.onNext(playerCallback.position) }, { /* Ignore exception */ })
        compositeDisposable.addAll(state, position)
    }

    fun unSubscribeQueue() {
        compositeDisposable.clear()
    }

    private fun handlePlayRequest(media: List<Media>, index: Int = 0) {
        if (media.isNotEmpty()) {
            queue.setQueue(media, index)
            play(queue.current)
        }
    }

    private fun handleAddRequest(media: Media) {
        queue.addQueue(media)
        onNextQueue()
    }

    private fun handleRemoveRequest(media: Media) {
        queue.removeQueue(media)
        onNextQueue()
    }

    private fun handleSeekRequest(position: Long) {
        playerCallback.seekTo(position)
    }

    private fun handlePrevRequest() {
        val position = TimeUnit.MILLISECONDS.toSeconds(playerCallback.position)
        playerCallback.invalidateCurrent()
        if (position <= 3) {
            play(queue.previous)
        } else {
            play(queue.current)
        }
    }

    private fun handlePauseRequest() {
        playerCallback.pause()
    }

    private fun handleResumeRequest() {
        play(queue.current)
    }

    private fun handleNextRequest() {
        play(queue.next)
    }

    private fun handleStopRequest() {
        playerCallback.stop()
        updatePlaybackState(PlaybackState.stopped(), 0)
    }

    private fun play(media: Media?) {
        playerCallback.play(media)
        serviceCallback.onPlaybackMediaChanged(media)
        updatePlaybackState(PlaybackState.playing(media), playerCallback.position)
        onNextQueue()
    }

    private fun updatePlaybackState(playbackState: PlaybackState, position: Long) {
        serviceCallback.onPlaybackStateChanged(playbackState, position)
        RxMusicPlayer.state.onNext(playbackState)
    }

    private fun onNextQueue() {
        RxMusicPlayer.queue.onNext(QueueData(queue.list, queue.currentIndex))
    }

}