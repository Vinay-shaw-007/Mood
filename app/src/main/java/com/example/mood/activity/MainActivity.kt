package com.liqvd.digibox.ui

import android.Manifest
import android.app.usage.StorageStats
import android.app.usage.StorageStatsManager
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.os.StrictMode
import android.os.storage.StorageManager
import android.os.storage.StorageManager.ACTION_MANAGE_STORAGE
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.liqvd.digibox.BuildConfig
import com.liqvd.digibox.DigiboxxApplication
import com.liqvd.digibox.R
import com.liqvd.digibox.data.util.SharedPreferenceManager
import com.liqvd.digibox.databinding.ActivityDigiboxxBinding
import com.liqvd.digibox.digifotos.data.utils.DigiPhotosSharedPreferenceManager
import com.liqvd.digibox.domain.usercases.FileUploadUseCase
import com.liqvd.digibox.migration.migrateDatabaseToV512
import com.liqvd.digibox.presentation.ui.filedetails.FileDetailsFragment
import com.liqvd.digibox.receiver.NetworkCheck
import com.liqvd.digibox.service.MusicPlayerService
import com.liqvd.digibox.utils.LocaleHelper
import com.liqvd.digibox.utils.logDebug
import com.liqvd.digibox.utils.permissions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _musicServiceObserver= MutableStateFlow<Boolean>(false)
    val musicServiceObserver = _musicServiceObserver.asStateFlow()



    private val musicServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? MusicPlayerService.MusicBinder
            musicService = binder?.getService()
            isMusicServiceBound = true
            _musicServiceObserver.value = true
            // Perform any operations with the musicService instance here
            logDebug(
                FileDetailsFragment.TAG,
                "Fragment = onServiceConnected Service connected, isMusicServiceBound = ${isMusicServiceBound()}"
            )
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isMusicServiceBound = false
            _musicServiceObserver.value = false
            logDebug(FileDetailsFragment.TAG, "Fragment = onServiceDisconnected Service disconnected")
        }
    }
    private var isMusicServiceBound = false

    lateinit var binding: ActivityMainBinding

    fun myBindService() {
        logDebug(
            FileDetailsFragment.TAG,
            "Fragment = bindService, , isMusicServiceBound = ${isMusicServiceBound()}"
        )
        val intent = Intent(this, MusicPlayerService::class.java)
        bindService(intent, musicServiceConnection, Context.BIND_AUTO_CREATE)
    }

    fun isMusicServiceBound(): Boolean {
        return isMusicServiceBound
    }

    fun setMusicServiceBound(b: Boolean) {
        isMusicServiceBound = b
    }

    fun musicServiceInstance(): MusicPlayerService? {
        return musicService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "This is a default channel for showing song."

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


    fun myUnBindServer() {
        if (isMusicServiceBound()) {
            logDebug(FileDetailsFragment.TAG, "Fragment = unbindService, , isMusicServiceBound = ${isMusicServiceBound()}")
            unbindService(musicServiceConnection)
            musicService = null // Set musicService instance to null
            isMusicServiceBound = false
            _musicServiceObserver.value = false
        }
    }

    override fun onDestroy() {
        Log.d(FileDetailsFragment.TAG, "Fragment = onDestroy")
        myUnBindServer()
    }
}
