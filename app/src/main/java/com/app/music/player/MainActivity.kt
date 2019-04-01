package com.app.music.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.orfium.rx.musicplayer.RxMusicPlayer
import com.orfium.rx.musicplayer.common.PlaybackState
import com.orfium.rx.musicplayer.media.Media
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RxMusicPlayer.start(this)

        val adapter = PopularAdapter(generateMedia())
        popularRecyclerView.layoutManager = LinearLayoutManager(this)
        popularRecyclerView.adapter = adapter

        compositeDisposable.add(
            RxMusicPlayer.state
                .distinctUntilChanged()
                .subscribe { state ->
                    when (state) {
                        is PlaybackState.Buffering -> {
                            Log.d("PlaybackState Buffering", state.media?.toString())
                            adapter.notifyDataSetChanged()
                        }
                        is PlaybackState.Playing -> {
                            Log.d("PlaybackState Playing", state.media?.toString())
                            adapter.notifyDataSetChanged()
                        }
                        is PlaybackState.Paused -> {
                            Log.d("PlaybackState Paused", state.media?.toString())
                            adapter.notifyDataSetChanged()
                        }
                        is PlaybackState.Completed -> {
                            Log.d("PlaybackState Completed", state.media?.toString())
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
        )

        compositeDisposable.add(
            RxMusicPlayer.position
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe { position -> Log.d(this.javaClass.name, position.toString()) }
        )

        compositeDisposable.add(
            RxMusicPlayer.queue
                .subscribe { queue -> Log.d(this.javaClass.name, queue.toString()) }
        )
    }

    private fun generateMedia(): List<Media> {
        return listOf(
            Media(
                id = 1,
                image = "https://res.cloudinary.com/apilama/image/fetch/f_auto,c_thumb,q_auto,w_350,h_350/https://s3-us-west-2.amazonaws.com/orfium-public/tracks/artwork/45c4ad6b21dc4aecad4bee0bafefb613.jpg",
                title = "Closer and Closer . . . 25.11.2018",
                artist = "Strobi-wan",
                streamUrl = "https://s3-us-west-2.amazonaws.com/orfium-public/tracks/8c7465df1f0c4e48af10ad4f6c17a2ef.mp3",
                duration = 7861
            ),
            Media(
                id = 2,
                image = "https://res.cloudinary.com/apilama/image/fetch/f_auto,c_thumb,q_auto,w_350,h_350/https://s3-us-west-2.amazonaws.com/orfium-public/tracks/artwork/4cb50512e2084a98afbec4cb5b9c7e45.png",
                title = "deadmau5 - Faxing Berlin (Ābstrct Remix)",
                artist = "Ābstrct",
                streamUrl = "https://s3-us-west-2.amazonaws.com/orfium-public/tracks/858cd93ac0b045409ce74297761c2924.mp3",
                duration = 436
            ),
            Media(
                id = 3,
                image = "https://res.cloudinary.com/apilama/image/fetch/f_auto,c_thumb,q_auto,w_350,h_350/https://s3-us-west-2.amazonaws.com/orfium-public/tracks/artwork/cccb02abd0a14329b8eda9393b5412d9.jpeg",
                title = "FooF   Crab Cam",
                artist = "FooF",
                streamUrl = "https://s3-us-west-2.amazonaws.com/orfium-public/tracks/999851a11288425f849ad3b05935167d.mp3",
                duration = 3286
            ),
            Media(
                id = 4,
                image = "https://res.cloudinary.com/apilama/image/fetch/f_auto,c_thumb,q_auto,w_350,h_350/https://s3-us-west-2.amazonaws.com/orfium-public/tracks/artwork/2c1667d2aecc48738301286417f283d9.jpg",
                title = "Dvorak 9th Syymphony Mvt 4 Midi-Performance",
                artist = "Paul T. McGraw",
                streamUrl = "https://s3-us-west-2.amazonaws.com/orfium-public/tracks%2Fd4eec723-0685-4f9b-8b72-bf62784e0e42-1543803052.mp3",
                duration = 684
            ),
            Media(
                id = 5,
                image = "https://res.cloudinary.com/apilama/image/fetch/f_auto,c_thumb,q_auto,w_350,h_350/https://s3-us-west-2.amazonaws.com/orfium-public/tracks/artwork/a0f30fd652174fcea649b3e4d62906a1.jpg",
                title = "Basic",
                artist = "KAZDFF",
                streamUrl = "https://s3-us-west-2.amazonaws.com/orfium-public/tracks%2Fdb6758b3-98f9-4387-857e-2ecac0d7fdd2-1543248241.mp3",
                duration = 98
            ),
            Media(
                id = 6,
                image = "https://res.cloudinary.com/apilama/image/fetch/f_auto,c_thumb,q_auto,w_350,h_350/https://s3-us-west-2.amazonaws.com/orfium-public/tracks/artwork/03ec006465e148829e8ad8f34d548e84.jpg",
                title = "Loaded Gun",
                artist = "THE SAME PERSONS",
                streamUrl = "https://s3-us-west-2.amazonaws.com/orfium-public/tracks/4713b1f220494ef2987036354a0e4fbe.mp3",
                duration = 185
            ),
            Media(
                id = 7,
                image = "https://res.cloudinary.com/apilama/image/fetch/f_auto,c_thumb,q_auto,w_350,h_350/https://s3-us-west-2.amazonaws.com/orfium-public/tracks/artwork/27853f2f7f0d41c5abcdf74fe55fcae2.png",
                title = "Paranoid",
                artist = "DUB.45",
                streamUrl = "https://s3-us-west-2.amazonaws.com/orfium-public/tracks%2F03b7cf58-a6a2-4aa6-99d6-cce770a31207-1544081034.mp3",
                duration = 128
            ),
            Media(
                id = 8,
                image = "https://res.cloudinary.com/apilama/image/fetch/f_auto,c_thumb,q_auto,w_350,h_350/https://s3-us-west-2.amazonaws.com/orfium-public/tracks/artwork/64fa944477b04dcb8c576a06e0a39b8e.png",
                title = "Oni Komokuten",
                artist = "Elo The Source",
                streamUrl = "https://s3-us-west-2.amazonaws.com/orfium-public/tracks/505562d128b94a9bbea5b56cce9777e6.mp3",
                duration = 255
            ),
            Media(
                id = 9,
                image = "https://res.cloudinary.com/apilama/image/fetch/f_auto,c_thumb,q_auto,w_350,h_350/https://s3-us-west-2.amazonaws.com/orfium-public/tracks/artwork/0b409a1eaa264675acce32b406a9abc4.jpg",
                title = "Eat My Toe . . . 08.12.2018",
                artist = "Strobi-wan",
                streamUrl = "https://s3-us-west-2.amazonaws.com/orfium-public/tracks/1a2add036a614c8c963468cd8bc7946f.mp3",
                duration = 6431
            )
        )
    }
}
