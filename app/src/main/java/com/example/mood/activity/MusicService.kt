package com.liqvd.digibox.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.liqvd.digibox.DigiboxxApplication
import com.liqvd.digibox.R
import com.liqvd.digibox.presentation.ui.filedetails.FileDetailsFragment
import com.liqvd.digibox.receiver.MusicNotificationReceiver
import com.liqvd.digibox.ui.DigiboxxActivity
import com.liqvd.digibox.utils.CommonUtils
import com.liqvd.digibox.utils.logDebug
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicPlayerService : Service() {
    companion object {
        const val TAG = "MusicPlayerService"
    }

    private var _playPauseBtn = MutableStateFlow(false) //playIcon = true , pauseIcon = false
    val playPauseBtn = _playPauseBtn.asStateFlow()
    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var audioFile: String
    private lateinit var audioName: String
    private var isAudioScreenVisible = true
    private lateinit var activity: DigiboxxActivity
    private lateinit var musicNotificationReceiver: MusicNotificationReceiver

    inner class MusicBinder : Binder() {
        fun getService(): MusicPlayerService {
            logDebug(FileDetailsFragment.TAG, "MusicPlayerService = MusicBinder")
            return this@MusicPlayerService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My music")
        logDebug(FileDetailsFragment.TAG, "MusicPlayerService = onBind")
        return binder
    }

    /** isAudioScreenVisible check whether the FileDetails screen is still visible or not */
    fun setAudioScreenVisibilityToDisable(mainActivity: DigiboxxActivity, value: Boolean) {
        activity = mainActivity
        isAudioScreenVisible = value
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logDebug(FileDetailsFragment.TAG, "MusicPlayerService = onStartCommand ${intent?.action}")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        logDebug(FileDetailsFragment.TAG, "MusicPlayerService = onCreate")
        mediaPlayer = MediaPlayer()
        mediaPlayer?.apply {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            setAudioAttributes(audioAttributes)
        }
        musicNotificationReceiver = MusicNotificationReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(DigiboxxApplication.PAUSE)
        intentFilter.addAction(DigiboxxApplication.NOTIFICATION_DISMISS_ACTION)
        registerReceiver(musicNotificationReceiver, intentFilter)
    }

    fun playMusic(filePath: String, fileName: String) {
        logDebug(FileDetailsFragment.TAG, "MusicPlayerService = playMusic")
        mediaPlayer?.apply {
            audioFile = filePath
            audioName = fileName
            reset()
            setDataSource(filePath)
            prepare()
            start()
            showMusicNotification()
            logDebug(
                FileDetailsFragment.TAG,
                "Song Duration ${CommonUtils().formatDuration(duration.toLong())}"
            )
        }
    }

    fun forwardFiveSeconds() {
        if (mediaPlayer?.currentPosition!! >= mediaPlayer?.duration!!.minus(5000)) return
        val position = mediaPlayer?.currentPosition?.plus(5000)
        logDebug(FileDetailsFragment.TAG, "MusicPlayerService = forwardFiveSeconds $position")
        position?.let { mediaPlayer?.seekTo(it) }
    }

    fun rewindFiveSeconds() {
        if (mediaPlayer?.currentPosition!! <= 5000) return
        val position = mediaPlayer?.currentPosition?.minus(5000)
        logDebug(FileDetailsFragment.TAG, "MusicPlayerService = rewindFiveSeconds $position")
        position?.let { mediaPlayer?.seekTo(it) }
    }


    fun pauseMusic() {
        logDebug(FileDetailsFragment.TAG, "MusicPlayerService = pauseMusic")
        logDebug(FileDetailsFragment.TAG, "isAudioScreenVisible = $isAudioScreenVisible")
        if (!isAudioScreenVisible) {
            stopMusic()
            activity.myUnBindServer()
        }
        mediaPlayer?.pause()
        showMusicNotification(R.drawable.ic_play_arrow, "Play")
        _playPauseBtn.value = true
    }

    fun isPlayingSameAudio(filePath: String): Boolean {
        return audioFile == filePath
    }

    fun getFormattedCurrentPosition(): String? {
        return mediaPlayer?.currentPosition?.let { CommonUtils().formatDuration(it.toLong()) }
    }

    fun getCurrentPosition(): Int? {
        return mediaPlayer?.currentPosition
    }

    fun getFormattedDuration(): String? {
        return mediaPlayer?.duration?.let { CommonUtils().formatDuration(it.toLong()) }
    }

    fun getDuration(): Int? {
        return mediaPlayer?.duration
    }

    fun resumeMusic() {
        logDebug(FileDetailsFragment.TAG, "MusicPlayerService = resumeMusic")
        mediaPlayer?.start()
        showMusicNotification()
        _playPauseBtn.value = false
    }

    fun musicIsPlaying(): Boolean? {
        logDebug(FileDetailsFragment.TAG, "MusicPlayerService = musicIsPlaying")
        return mediaPlayer?.isPlaying
    }

    fun stopMusic() {
        logDebug(FileDetailsFragment.TAG, "MusicPlayerService = stopMusic")
        mediaPlayer?.stop()
        mediaPlayer?.reset()
    }

    fun seekTo(progress: Int) {
        mediaPlayer?.seekTo(progress)
    }

    fun showMusicNotification(
        playPauseButton: Int = R.drawable.ic_pause,
        playPauseText: String = "Pause"
    ) {
//        val notificationIntent = Intent(baseContext, DigiboxxActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//            action = "OPEN_FILE_DETAILS_FRAGMENT"
//        }
//        val notificationPendingIntent = PendingIntent.getActivity(baseContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val pauseIntent = Intent(
            baseContext,
            MusicNotificationReceiver::class.java
        ).setAction(DigiboxxApplication.PAUSE)
        val pausePendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE)

        val rewindIntent = Intent(
            baseContext,
            MusicNotificationReceiver::class.java
        ).setAction(DigiboxxApplication.REWIND)
        val rewindPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, rewindIntent, PendingIntent.FLAG_IMMUTABLE)

        val forwardIntent = Intent(
            baseContext,
            MusicNotificationReceiver::class.java
        ).setAction(DigiboxxApplication.FORWARD)
        val forwardPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, forwardIntent, PendingIntent.FLAG_IMMUTABLE)


        val deleteIntent = Intent(
            baseContext,
            MusicNotificationReceiver::class.java
        ).setAction(DigiboxxApplication.NOTIFICATION_DISMISS_ACTION)

        val deletePendingIntent = PendingIntent.getService(
            baseContext,
            0,
            deleteIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(baseContext, DigiboxxApplication.CHANNEL_ID)
            .setOngoing(true)
            .setContentTitle(audioName)
            .setSmallIcon(R.drawable.company_logo)
//            .setLargeIcon(
//                BitmapFactory.decodeResource(
//                    resources,
//                    R.drawable.company_logo
//                )
//            )// here the drawable image is not showing in the notification
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .addAction(R.drawable.ic_replay, "Rewind", rewindPendingIntent)
            .addAction(playPauseButton, playPauseText, pausePendingIntent)
            .addAction(R.drawable.ic_forward, "Forward", forwardPendingIntent)
            .setDeleteIntent(deletePendingIntent)
            .build()


        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {
        super.onDestroy()
        logDebug(FileDetailsFragment.TAG, "MusicPlayerService = onDestroy")
        mediaPlayer?.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
        unregisterReceiver(musicNotificationReceiver)
        mediaPlayer = null
    }
}
