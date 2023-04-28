package com.liqvd.digibox.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.widget.Toast
import com.google.android.exoplayer2.util.Log
import com.liqvd.digibox.DigiboxxApplication
import com.liqvd.digibox.R
import com.liqvd.digibox.presentation.ui.filedetails.FileDetailsFragment
import com.liqvd.digibox.ui.DigiboxxActivity
import com.liqvd.digibox.utils.logDebug
import com.liqvd.digibox.utils.notifications.showNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MusicNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        logDebug(FileDetailsFragment.TAG, "From broadcast = ${intent?.action}")
        when (intent?.action) {
            DigiboxxApplication.PAUSE -> {
                DigiboxxActivity.musicService?.apply {
                    if (this.musicIsPlaying() == true) {
                        Log.d(FileDetailsFragment.TAG, "Pause")
                        this.showMusicNotification(R.drawable.ic_play_arrow)
                        this.pauseMusic()
                    } else {
                        Log.d(FileDetailsFragment.TAG, "Play")
                        this.showMusicNotification()
                        this.resumeMusic()
                    }
                }
            }
            DigiboxxApplication.REWIND -> {
                DigiboxxActivity.musicService?.apply {
                    rewindFiveSeconds()
                }
            }
            DigiboxxApplication.FORWARD -> {
                DigiboxxActivity.musicService?.apply {
                    forwardFiveSeconds()
                }
            }
            DigiboxxApplication.NOTIFICATION_DISMISS_ACTION -> {
                Toast.makeText(context, "Notification dismiss", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
