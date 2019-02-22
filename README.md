[![](https://jitpack.io/v/Orfium/RxMusicPlayer-android.svg)](https://jitpack.io/#Orfium/RxMusicPlayer-android)

# RxMusicPlayer

An android music player using ExoPlayer and RxJava2.

## Introduction

RxMusicPlayer is a part of our music player in Orfium new android application.     
Using subjects and sealed classes, media manager can emit the latest state of exoplayer, allowing views and classes to observe it and react according to its state, without the need to have a reference to the media manager or exoplayer itself.

## Initialization
Before start using RxMusicPlayer you have to call the method that starts the Media Service

```kotlin
RxMusicPlayer.start(context)
```
## Usage

Create a [Media](https://github.com/Orfium/RxMusicPlayer-android/blob/master/rxmusicplayer/src/main/java/com/orfium/rx/musicplayer/media/Media.kt) item class that contains all the necessary data information and call playStop extension function on it.

Example:
```kotlin
val media = Media(
            id = 1, title = "Closer and Closer . . . 25.11.2018", artist = "Strobi-wan", duration = 7861, 
            image = "https://s3-us-west-2.amazonaws.com/orfium-public/tracks/artwork/45c4ad6b21dc4aecad4bee0bafefb613.jpg",
            streamUrl = "https://s3-us-west-2.amazonaws.com/orfium-public/tracks/8c7465df1f0c4e48af10ad4f6c17a2ef.mp3"
        )
media.playStop()
```
You can also check if a Media item is currently playing
```kotlin
if (media.isPlaying())
```
or add/remove it from queue
```kotlin
media.addQueue()

media.removeQueue()
```
If you wish not to use the [extension functions](https://github.com/Orfium/RxMusicPlayer-android/blob/master/rxmusicplayer/src/main/java/com/orfium/rx/musicplayer/common/Extensions.kt) you can use RxMusicPlayer action to emit a new action that the MediaManager will handle. [Here](https://github.com/Orfium/RxMusicPlayer-android/blob/master/rxmusicplayer/src/main/java/com/orfium/rx/musicplayer/common/Action.kt) is the full list of actions that the MediaManager can handle

Example:
```kotlin
RxMusicPlayer.action.onNext(Action.pause())

or

RxMusicPlayer.action.onNext(Action.seek(position))
```

To observe the changes on media player state just subscribe to RxMusicPlayer state, that will emit the current PlaybackState along with the current Media item on Queue. When a new observer subscribes to RxMusicPlayer state, it immediately gets notified about the current PlaybackState. You can find all states [here](https://github.com/Orfium/RxMusicPlayer-android/blob/master/rxmusicplayer/src/main/java/com/orfium/rx/musicplayer/common/PlaybackState.kt)
```kotlin
RxMusicPlayer.state
            .distinctUntilChanged()
            .subscribe { state ->
                when (state) {
                    is PlaybackState.Buffering -> /* Your code */
                    is PlaybackState.Playing -> showPlaying(state.media)
                }
            }
```
RxMusicPlayer allows you to observe changes in media queue list and the current playback position of exoplayer
```kotlin
RxMusicPlayer.queue
                .subscribe { queueData -> /* Your code */ }

RxMusicPlayer.position
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe { position -> /* Your code */ }
```

## Setup
The latest libary version is [![](https://jitpack.io/v/Orfium/RxMusicPlayer-android.svg)](https://jitpack.io/#Orfium/RxMusicPlayer-android)

Add the JitPack repository in your build.gradle (top level module):
```gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```
And add the dependency in the build.gradle of the module:
```gradle
implementation 'com.github.Orfium:RxMusicPlayer-android:LATEST_VERSION'
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.
