package com.example.mood.repository;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.mood.model.SongList;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllSongRepo{

    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(1);
    private ArrayList<SongList> songLists;
    private final Application application;
    public AllSongRepo(Application application) {
        this.application = application;
    }
//    public void fetchAllSongs(){
//        databaseWriteExecutor.execute(this::fetchSongs);
//    }

    public ArrayList<SongList> getAllSongs(){
        databaseWriteExecutor.execute(this::fetchSongs);
        return songLists;
    }

    void fetchSongs() {
        songLists = new ArrayList<>();
        ContentResolver contentResolver = application.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null,selection,  null, sortOrder);
        if (cursor != null && cursor.getCount() > 0) {
            int songId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            int date = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);
            int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            while (cursor.moveToNext()) {
                Long SongId = cursor.getLong(songId);
                String songTitle = cursor.getString(title);
                String songArtist = cursor.getString(artist);
                String songData = cursor.getString(data);
                Long songDate = cursor.getLong(date);
                long albumId = cursor.getLong(albumColumn);
                Uri uri1 = Uri.parse("content://media/external/audio/albumart");
                Uri image = Uri.withAppendedPath(uri1, ""+albumId);
                // Save to audioList
                songLists.add(new SongList(String.valueOf(SongId), songTitle, songArtist, songData, String.valueOf(songDate), image.toString()));
//                Log.d(TAG, "fetchSongs: songId " + songId);
//                Log.d(TAG, "fetchSongs: songTitle " + songTitle);
//                Log.d(TAG, "fetchSongs: songArtist " + songArtist);
//                Log.d(TAG, "fetchSongs: songData " + songData);
//                Log.d(TAG, "fetchSongs: songDate " + songDate);
//                Log.d(TAG, "fetchSongs: image " + image);
            }
        }else Toast.makeText(application, "Something Went Wrong.", Toast.LENGTH_SHORT).show();
        assert cursor != null;
        cursor.close();
    }
}
