package com.sherafatpour.advancevideoplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.common.collect.ImmutableList
import com.sherafatpour.advancevideoplayer.databinding.ActivityPlayerBinding
import androidx.activity.addCallback
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout

class PlayerActivity : AppCompatActivity(), Player.Listener, View.OnClickListener {
    private lateinit var player: ExoPlayer
    private lateinit var title: TextView
    private lateinit var playerView: PlayerView
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var lock: ImageView
    private lateinit var unLock: ImageView
    private lateinit var scaling: ImageView
    private lateinit var root: RelativeLayout
    private lateinit var recyclerViewIcons: RecyclerView
    lateinit var controlsMode: ControlsMode
    lateinit var playbackIconsAdapter: PlaybackIconsAdapter
    private var expand: Boolean = false
    private var darkMode: Boolean = false

    private val iconModelArrayList = ArrayList<IconModel>()

    //horizontal recyclerview variables
    var iconModelLive = MutableLiveData<List<IconModel>>()

    enum class ControlsMode { LOCK, FULLSCREEN }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        player = ExoPlayer.Builder(this).build()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playerView = binding.exoplayerView
        title = playerView.findViewById(R.id.video_title)
        lock = playerView.findViewById(R.id.lock)
        unLock = playerView.findViewById(R.id.unlock)
        root = playerView.findViewById(R.id.root_layout)
        scaling = playerView.findViewById(R.id.scaling)
        recyclerViewIcons = playerView.findViewById(R.id.recyclerView_icon)

        //title.text = "TIIIITLE"
        playerView.player = player
        playerView.keepScreenOn = true
        player.playWhenReady = true

        lock.setOnClickListener(this)
        unLock.setOnClickListener(this)
        root.setOnClickListener(this)
        scaling.setOnClickListener(this)

        setupPlayer()
        initIconsRecyclerview()

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
        setFullScreen()
    }

    private fun initIconsRecyclerview() {


        iconModelArrayList.add(IconModel(R.drawable.ic_right, "", IconType.BACK))
        iconModelArrayList.add(IconModel(R.drawable.ic_night_mode, "Night", IconType.NIGHT))
        iconModelArrayList.add(IconModel(R.drawable.ic_volume_off, "Mute", IconType.MUTE))
        iconModelArrayList.add(IconModel(R.drawable.ic_rotate, "Rotate", IconType.ROTATE))

        iconModelLive.postValue(iconModelArrayList)

        iconModelLive.observe(this) {
            playbackIconsAdapter = PlaybackIconsAdapter(it) { iconModel, position ->
                iconItemClicked(iconModel, position)

            }
            recyclerViewIcons.apply {
                adapter = playbackIconsAdapter
                layoutManager =
                    LinearLayoutManager(this@PlayerActivity, RecyclerView.HORIZONTAL, true)
            }
        }


    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun iconItemClicked(partItem: IconModel, position: Int) {

        when (partItem.type) {
            IconType.NIGHT -> {
                if (darkMode) {
                    binding.nightMode.visibility = View.GONE
                    iconModelArrayList[position] =
                        IconModel(R.drawable.ic_night_mode, "Night", IconType.NIGHT)
                    playbackIconsAdapter.notifyItemChanged(position)
                    darkMode = false

                } else {
                    binding.nightMode.visibility = View.VISIBLE
                    iconModelArrayList[position] =
                        IconModel(R.drawable.ic_night_mode, "Day", IconType.NIGHT)
                    playbackIconsAdapter.notifyItemChanged(position)

                    darkMode = true

                }
            }
            IconType.MUTE -> {

                player.deviceVolume = 0
                iconModelArrayList[position] =
                    IconModel(R.drawable.ic_volume, "unMute", IconType.VOLUME)
                playbackIconsAdapter.notifyItemChanged(position)


            }
            IconType.ROTATE -> {
                Toast.makeText(
                    this, "ROTATE $position",
                    Toast.LENGTH_SHORT
                ).show()
            }
            IconType.BACK -> {

                if (!expand) {

                    iconModelArrayList.add(
                        IconModel(
                            R.drawable.ic_brightness,
                            "Brightness",
                            IconType.BRIGHTNESS
                        )
                    )
                    iconModelArrayList.add(
                        IconModel(
                            R.drawable.ic_equalizer,
                            "Equalizer",
                            IconType.EQUALIZER
                        )
                    )
                    iconModelArrayList.add(IconModel(R.drawable.ic_speed, "Speed", IconType.SPEED))
                    iconModelArrayList.add(
                        IconModel(
                            R.drawable.ic_subtitles,
                            "Subtitle",
                            IconType.SUBTITLE
                        )
                    )
                    iconModelArrayList[position] =
                        IconModel(R.drawable.ic_left, "", IconType.COLLAPSE)

                    iconModelLive.postValue(iconModelArrayList)

                    expand = !expand

                }
            }
            IconType.BRIGHTNESS -> {

                val brightnessDialog = BrightnessDialog()
                brightnessDialog.show(supportFragmentManager,"dialog")
                playbackIconsAdapter.notifyItemChanged(position)

            }
            IconType.EQUALIZER -> {


                val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                if ((intent.resolveActivity(packageManager)) !=null){
                    startActivityForResult(intent,123)

                }else{

                    Toast.makeText(
                        this, "No Equalizer Found",
                        Toast.LENGTH_SHORT
                    ).show()

                }




                playbackIconsAdapter.notifyItemChanged(position)


            }
            IconType.SPEED -> {
                Toast.makeText(
                    this, "SPEED $position",
                    Toast.LENGTH_SHORT
                ).show()
            }
            IconType.SUBTITLE -> {
                Toast.makeText(
                    this, "SUBTITLE $position",
                    Toast.LENGTH_SHORT
                ).show()
            }
            IconType.COLLAPSE -> {


                iconModelArrayList.clear()
                iconModelArrayList.add(IconModel(R.drawable.ic_right, "", IconType.BACK))


                iconModelArrayList.add(IconModel(R.drawable.ic_night_mode, "Night", IconType.NIGHT))



                if (player.deviceVolume > 0) {
                    iconModelArrayList.add( IconModel(R.drawable.ic_volume_off, "Mute", IconType.MUTE))
                } else {
                    iconModelArrayList.add(IconModel(R.drawable.ic_volume, "unMute", IconType.VOLUME))

                }


                iconModelArrayList.add(IconModel(R.drawable.ic_rotate, "Rotate", IconType.ROTATE))
                iconModelLive.postValue(iconModelArrayList)

                expand = !expand


            }

            IconType.VOLUME -> {
                player.deviceVolume = 8
                iconModelArrayList[position] =
                    IconModel(R.drawable.ic_volume_off, "Mute", IconType.MUTE)
                playbackIconsAdapter.notifyItemChanged(position)

            }
            else -> {}
        }
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

    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onClick(v: View?) {


        when (v!!.id) {

            R.id.lock -> {
                controlsMode = ControlsMode.FULLSCREEN
                root.visibility = View.VISIBLE
                lock.visibility = View.INVISIBLE
                Toast.makeText(this@PlayerActivity, "unlocked", Toast.LENGTH_SHORT).show()
            }
            R.id.unlock -> {
                controlsMode = ControlsMode.LOCK
                root.visibility = View.INVISIBLE
                lock.visibility = View.VISIBLE
                Toast.makeText(this@PlayerActivity, "Locked", Toast.LENGTH_SHORT).show()
            }
            R.id.scaling -> {}


        }

    }

    private val firstListener: View.OnClickListener = View.OnClickListener {

        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        player.videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT
        scaling.setImageResource(R.drawable.ic_fullscreen)
        Toast.makeText(this@PlayerActivity, "Full Screen", Toast.LENGTH_SHORT).show()

        scaling.setOnClickListener(secondListener)

    }

    private val secondListener: View.OnClickListener = View.OnClickListener {

        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        player.videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT
        scaling.setImageResource(R.drawable.ic_zoom)
        Toast.makeText(this@PlayerActivity, "Zoom", Toast.LENGTH_SHORT).show()

        scaling.setOnClickListener(thirdListener)

    }

    private val thirdListener: View.OnClickListener = View.OnClickListener {

        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        player.videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT
        scaling.setImageResource(R.drawable.ic_fit)
        Toast.makeText(this@PlayerActivity, "Fit", Toast.LENGTH_SHORT).show()

        scaling.setOnClickListener(firstListener)

    }


}