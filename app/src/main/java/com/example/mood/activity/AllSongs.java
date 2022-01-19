package com.example.mood.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mood.adapter.SongAdapter;
import com.example.mood.databinding.ActivityAllSongBinding;
import com.example.mood.model.SongList;

import java.util.ArrayList;
import java.util.Objects;

public class AllSongs extends AppCompatActivity implements SongAdapter.OnSongClicked {

    private ActivityAllSongBinding binding;
    private RecyclerView mRecyclerView;
    private SongAdapter mAdapter;
    private ArrayList<SongList> songLists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllSongBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setElevation(30);
        mRecyclerView = binding.rvSongList;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SongAdapter(this, songLists, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void OnItemClicked(SongList songList, int position) {

    }

//    private void swipeLeftToPlayFunction() {
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = (int) viewHolder.itemView.getTag();
//                Intent intent = new Intent(AllSongs.this, SongPlayer.class);
//                intent.putExtra("All Songs",songLists);
//                intent.putExtra("Position", position);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        }).attachToRecyclerView(mRecyclerView);
//    }

//    private void initializeRecyclerView() {
//        mRecyclerView = findViewById(R.id.rv_SongList);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        SongAdapter mAdapter = new SongAdapter(this, songLists, this);
//        mRecyclerView.setAdapter(mAdapter);
//    }

//    private void grantPermission() {
//        if (ContextCompat.checkSelfPermission(
//                this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
//                PackageManager.PERMISSION_GRANTED) {
//            // You can use the API that requires the permission.
////            fetchSongs();
//        } else {
//            // You can directly ask for the permission.
//            // The registered ActivityResultCallback gets the result of this request.
//            requestPermissionLauncher.launch(
//                    Manifest.permission.READ_EXTERNAL_STORAGE);
//        }
//    }
//    private final ActivityResultLauncher<String> requestPermissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    // Permission is granted. Continue the action or workflow in your
//                    // app.
////                    fetchSongs();
//                } else {
//                    // Explain to the user that the feature is unavailable because the
//                    // features requires a permission that the user has denied. At the
//                    // same time, respect the user's decision. Don't link to system
//                    // settings in an effort to convince the user to change their
//                    // decision.
//                    Toast.makeText(this, "Permission required. ", Toast.LENGTH_SHORT).show();
//                }
//            });


//    @Override
//    public void OnItemClicked(SongList songList, int position) {
////        Intent intent = new Intent(this, SongPlayer.class);
////        intent.putExtra("All Songs",songLists);
////        intent.putExtra("Position", position);
////        startActivity(intent);
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        isStopActivity = true;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (isStopActivity) {
////            fetchSongs();
//            isStopActivity = false;
//        }
//    }

//Fetching song by File system.

//    private ArrayList<File> getSongs(File file){
//        ArrayList<File> allSongs = new ArrayList<>();
//        File[] files = file.listFiles();
//        assert files != null;
//        for (File singleFile : files){
//            if (singleFile.isDirectory() && !singleFile.isHidden()){
//                allSongs.addAll(getSongs(singleFile));
//            }else{
//                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){//
//                    allSongs.add(singleFile);
//                }
//            }
//        }
//        return allSongs;
//    }
//    private void displaySong(){
//        final ArrayList<File> allSongs = getSongs(Environment.getExternalStorageDirectory());
//        String[] items = new String[allSongs.size()];
//        for (int i = 0; i < allSongs.size(); i++){
//            items[i] = allSongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
//            Log.d(TAG, "displaySong: Song "+(i+1)+" = "+items[i]);
////            Log.d(TAG, "displaySong: Song "+(i+1)+" = "+allSongs.get(i).getAbsolutePath().replace(".mp3", "").replace(".wav", ""));
////            Log.d(TAG, "displaySong: Song "+(i+1)+" = "+allSongs.get(i).getPath().replace(".mp3", "").replace(".wav", ""));
////            Log.d(TAG, "displaySong: Song "+(i+1)+" = "+allSongs.get(i).getAbsoluteFile());
//        }
//    }
}