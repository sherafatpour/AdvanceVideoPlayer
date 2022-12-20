package com.sherafatpour.advancevideoplayer

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.cache.CacheDataSource.EventListener
import com.google.common.collect.ImmutableList
import com.sherafatpour.advancevideoplayer.databinding.ActivityPlayerBinding
import androidx.activity.addCallback

class PlayerActivity : AppCompatActivity(), Player.Listener {
    private lateinit var player: ExoPlayer
    private lateinit var title: TextView
    private lateinit var playerView: PlayerView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        player = ExoPlayer.Builder(this).build()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playerView = binding.exoplayerView
        title = playerView.findViewById(R.id.video_title)
        title.text = "TIIIITLE"
        playerView.player = player
        playerView.keepScreenOn = true
        setupPlayer()
        addMP3()
        addMP4Files()


        // restore playstate on Rotation
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("mediaItem") != 0) {
                val restoredMediaItem = savedInstanceState.getInt("mediaItem")
                val seekTime = savedInstanceState.getLong("SeekTime")
                player.seekTo(restoredMediaItem, seekTime)


            }
        }

        onBackPress()
      //  setFullScreen()
    }


    private fun addMP4Files() {
        val mediaItem1 = MediaItem.fromUri(getString(R.string.media_url_mkv))
        val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4))
        val mediaItem2 = MediaItem.fromUri(getString(R.string.myTestMp4))
        val newItems: List<MediaItem> = ImmutableList.of(
            mediaItem1,
            mediaItem,
            mediaItem2
        )
        player.addMediaItems(newItems)
        player.prepare()
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.exoplayer_view)
        playerView.player = player
        player.addListener(this)
        playError()
    }

    private fun playError() {
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(this@PlayerActivity, "Video playing Error", Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.INVISIBLE

            }
        })
    }

    private fun addMP3() {
        // Build the media item.
        val mediaItem = MediaItem.fromUri(getString(R.string.test_mp3))
        player.setMediaItem(mediaItem)
        // Set the media item to be played.
        player.setMediaItem(mediaItem)
        // Prepare the player.
        player.prepare()
    }


    override fun onStop() {
        super.onStop()
        player.release()
    }

    override fun onResume() {
        super.onResume()
        setupPlayer()
        addMP3()
        addMP4Files()
    }


    // handle loading
    override fun onPlaybackStateChanged(state: Int) {
        if (state == Player.STATE_BUFFERING) {
            binding.progressBar.visibility = View.VISIBLE

        } else if (state == Player.STATE_READY) {
            binding.progressBar.visibility = View.INVISIBLE

        }
    }

    //get Title from metadata
    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {

        title.text = mediaMetadata.title ?: mediaMetadata.displayTitle ?: "no title found"

    }


    // save details if Activity is destroyed
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: " + player.currentPosition)
        // current play position
        outState.putLong("SeekTime", player.currentPosition)
        // current mediaItem
        outState.putInt("mediaItem", player.currentMediaItemIndex)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onSaveInstanceState: " + player.currentPosition)
        player.stop()
    }

    companion object {
        private const val TAG = "PlayerActivity"
    }

    private fun onBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (player.isPlaying) {
                    player.stop()
                }
                finish()

            }
        })
    }

    private fun setFullScreen(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }


}