package com.edu.s1572691.songle.songle;

import java.io.Serializable;

/**
 * Created by Rusab Asher on 22/10/2017.
 */

public class SongParse implements Serializable {
    public Songs getSong() {
        return Songs;
    }

    public void setSong(Songs Songs) {
        this.Songs = Songs;
    }

    private Songs Songs;

}
class Songs {
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public Song[] getSong() {
        return Song;
    }

    public void setSong(Song[] Song) {
        this.Song = Song;
    }

    private String timestamp;
    private String root;
    private Song[] Song;

}
class Song {
    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    private String Number;
    private String Artist;
    private String Title;
    private String Link;
}
