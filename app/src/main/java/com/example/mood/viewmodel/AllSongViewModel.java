package com.example.mood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.mood.repository.AllSongRepo;

public class AllSongViewModel extends AndroidViewModel {
    private final AllSongRepo repo;
    public AllSongViewModel(@NonNull Application application) {
        super(application);
        repo = new AllSongRepo(application);
    }
//    public void fetchSongs(){
//        repo.fetchAllSongs();
//    }
//    LiveData<SongList> getAllSongs(){
//        return repo.getAllSongs();
//    }
}
