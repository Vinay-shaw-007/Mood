package com.example.mood.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SongList implements Parcelable {
    private final String songId, songTitle, songArtist, songData, songDate, songImage;

    public SongList(String songId, String songTitle, String songArtist, String songData, String songDate, String songImage) {
        this.songId = songId;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songData = songData;
        this.songDate = songDate;
        this.songImage = songImage;
    }

    protected SongList(Parcel in) {
        songId = in.readString();
        songTitle = in.readString();
        songArtist = in.readString();
        songData = in.readString();
        songDate = in.readString();
        songImage = in.readString();
    }

    public static final Creator<SongList> CREATOR = new Creator<SongList>() {
        @Override
        public SongList createFromParcel(Parcel in) {
            return new SongList(in);
        }

        @Override
        public SongList[] newArray(int size) {
            return new SongList[size];
        }
    };

    public String getSongId() {
        return songId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getSongData() {
        return songData;
    }

    public String getSongDate() {
        return songDate;
    }

    public String getSongImage() {
        return songImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songId);
        dest.writeString(songTitle);
        dest.writeString(songArtist);
        dest.writeString(songData);
        dest.writeString(songDate);
        dest.writeString(songImage);
    }
}
