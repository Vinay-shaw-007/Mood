package com.example.mood.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mood.R;
import com.example.mood.model.SongList;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private final ArrayList<SongList> songLists;
    private final Context context;
    private final OnSongClicked onSongClicked;
    @SuppressLint("NotifyDataSetChanged")
    public SongAdapter(Context context, ArrayList<SongList> songLists, OnSongClicked onSongClicked) {
        this.context = context;
        this.onSongClicked = onSongClicked;
        this.songLists = songLists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_details,parent, false);
        return new SongViewHolder(view, onSongClicked, songLists);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongList songList = songLists.get(position);
        holder.songTitle.setText(songList.getSongTitle());
        holder.songArtist.setText(songList.getSongArtist());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return songLists.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView songIcon;
        TextView songTitle, songArtist;
        OnSongClicked onSongClicked;
        ArrayList<SongList> songLists;
        public SongViewHolder(@NonNull View itemView, OnSongClicked onSongClicked, ArrayList<SongList> songLists) {
            super(itemView);
            songIcon = itemView.findViewById(R.id.song_image);
            songTitle = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
            itemView.setOnClickListener(this);
            this.onSongClicked = onSongClicked;
            this.songLists = songLists;
        }

        @Override
        public void onClick(View v) {
            onSongClicked.OnItemClicked(songLists.get(getAdapterPosition()), getAdapterPosition());
        }
    }
    public interface OnSongClicked{
        void OnItemClicked(SongList songList, int position);
    }
}
