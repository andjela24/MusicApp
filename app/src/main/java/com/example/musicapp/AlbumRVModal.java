package com.example.musicapp;

public class AlbumRVModal {
    //kreiranje polja za svaki objekat album
    public String album_type;
    public String artistName;
    public String external_ids;
    public String external_urls;
    public String href;
    public String id;
    public String imageUrl;
    public String label;
    public String name;
    public int popularity;
    public String release_date;
    public int total_tracks;
    public String type;

    //kreiranje konstruktora
    public AlbumRVModal(String album_type, String artistName, String external_ids, String external_urls, String href, String id, String imageUrl, String label, String name, int popularity, String release_date, int total_tracks, String type) {
        this.album_type = album_type;
        this.artistName = artistName;
        this.external_ids = external_ids;
        this.external_urls = external_urls;
        this.href = href;
        this.id = id;
        this.imageUrl = imageUrl;
        this.label = label;
        this.name = name;
        this.popularity = popularity;
        this.release_date = release_date;
        this.total_tracks = total_tracks;
        this.type = type;
    }
}
