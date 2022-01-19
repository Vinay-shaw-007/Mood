package com.example.mood.activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mood.R;
import com.example.mood.model.SongList;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SongPlayer extends AppCompatActivity {

    private static final String TAG = "SongPlayer";
    private TextView startTime, songDuration, songName;
    private SeekBar seekBar;
    private ImageView previous, play, pause, next, shuffle, repeat;
    private int position;
    private ArrayList<SongList> songListArrayList;
    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    private Runnable runnable;
    private boolean isShuffle = false, isRepeat = false;
    private LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);
        hooks();
        intentData();
        ThreadWork();
        songPlayer();
        eventHandler();
        songName.setSelected(true);
    }

    private void ThreadWork() {
        mediaPlayer = MediaPlayer.create(this, Uri.parse(songListArrayList.get(position).getSongData()));
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 1000);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
    }

    private void songPlayer() {
        mediaPlayer = MediaPlayer.create(this, Uri.parse(songListArrayList.get(position).getSongData()));
        songName.setText(songListArrayList.get(position).getSongTitle());
        int duration = mediaPlayer.getDuration();
        String sDuration = convertIntoTimeFormat(duration);
        songDuration.setText(sDuration);
        play.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        handler.postDelayed(runnable, 0);
        mediaPlayer.setOnCompletionListener(mp -> {
            if (isShuffle){
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                int val = generateRandomNumber(songListArrayList.size());
                if (val == position) val = generateRandomNumber(songListArrayList.size());
                position = val;
                songPlayer();
            }else if (isRepeat){
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            }else {
                pause.setVisibility(View.GONE);
                mediaPlayer.seekTo(0);
                play.setVisibility(View.VISIBLE);
            }
        });
    }
    private static int generateRandomNumber(int range){
        Random random = new Random();
        return random.nextInt(range);
    }
    private void eventHandler() {
        play.setOnClickListener(v -> {
            lottieAnimationView.playAnimation();
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            mediaPlayer.start();
            handler.postDelayed(runnable, 0);
        });

        pause.setOnClickListener(v -> {
            lottieAnimationView.pauseAnimation();
            pause.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
            mediaPlayer.pause();
            handler.removeCallbacks(runnable);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar1, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }
                startTime.setText(convertIntoTimeFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar1) {
            }
        });

        previous.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            if (position != 0){
                position -= 1;
            }else {
                position = songListArrayList.size() - 1;
            }
            songPlayer();
        });
        next.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            if (position != songListArrayList.size()-1){
                position += 1;
            }else {
                position = 0;
            }
            songPlayer();
        });
        repeat.setOnClickListener(v -> {
            if (isShuffle) {
                isShuffle = false;
                isRepeat = true;
            }else isRepeat = !isRepeat;
            if (isRepeat) {
                Toast.makeText(SongPlayer.this, "Repeat ON", Toast.LENGTH_SHORT).show();
                int seenMsgColor = Color.parseColor("#1FB9FF");
                repeat.setColorFilter(seenMsgColor);
                int seenMsgColor1 = Color.parseColor("#E21468");
                shuffle.setColorFilter(seenMsgColor1);
            }
            else {
                Toast.makeText(SongPlayer.this, "Repeat OFF", Toast.LENGTH_SHORT).show();
                int seenMsgColor = Color.parseColor("#E21468");
                repeat.setColorFilter(seenMsgColor);
            }
        });
        shuffle.setOnClickListener(v -> {
            if (isRepeat){
                isRepeat = false;
                isShuffle = true;
            }else isShuffle = !isShuffle;
            if (isShuffle){
                Toast.makeText(SongPlayer.this, "Shuffle ON", Toast.LENGTH_SHORT).show();
                int seenMsgColor = Color.parseColor("#1FB9FF");
                shuffle.setColorFilter(seenMsgColor);
                int seenMsgColor1 = Color.parseColor("#E21468");
                repeat.setColorFilter(seenMsgColor1);
            }
            else {
                Toast.makeText(SongPlayer.this, "Shuffle OFF", Toast.LENGTH_SHORT).show();
                int seenMsgColor = Color.parseColor("#E21468");
                shuffle.setColorFilter(seenMsgColor);
            }
        });
    }

    private String convertIntoTimeFormat(int duration) {
        return String.format(Locale.getDefault(),"%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    private void intentData() {
        Intent i = getIntent();
        position = i.getIntExtra("Position", 0);
        songListArrayList = i.getParcelableArrayListExtra("All Songs");
        Log.d(TAG, "onCreate: position "+position);
        Log.d(TAG, "onCreate: songList "+songListArrayList.get(position));
    }

    private void hooks() {
        startTime = findViewById(R.id.start_time);
        songDuration = findViewById(R.id.song_duration);
        songName = findViewById(R.id.song_name);
        seekBar = findViewById(R.id.seekBar);
        previous = findViewById(R.id.btn_previous);
        play = findViewById(R.id.btn_play);
        pause = findViewById(R.id.btn_pause);
        next = findViewById(R.id.btn_forward);
        repeat = findViewById(R.id.btn_repeat);
        shuffle = findViewById(R.id.btn_shuffle);
        lottieAnimationView = findViewById(R.id.lottie_animation);
        Objects.requireNonNull(getSupportActionBar()).setElevation(30);
        getSupportActionBar().setTitle("Now Playing");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            handler.removeCallbacks(runnable);
            mediaPlayer = null;
        }
//        updateSeekBar.interrupt();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lottieAnimationView.playAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        lottieAnimationView.pauseAnimation();
    }

    @Override
    public void onBackPressed() {
        lottieAnimationView.pauseAnimation();
        super.onBackPressed();
        startActivity(new Intent(this, AllSongs.class));
        finishAffinity();
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}