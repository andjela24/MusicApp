package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AlbumRVAdapter extends RecyclerView.Adapter<AlbumRVAdapter.ViewHolder> {
    //kreiranje niza za listu i kontekst
    private ArrayList<AlbumRVModal> albumRVModalArrayList;
    private Context context;

    //kreiranje konstruktora
    public AlbumRVAdapter(ArrayList<AlbumRVModal> albumRVModalArrayList, Context context) {
        this.albumRVModalArrayList = albumRVModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AlbumRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //ubacivanje layout fajla
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumRVAdapter.ViewHolder holder, int position) {
        //podesavanje podataka na tekst i sliku
        AlbumRVModal albumRVModal = albumRVModalArrayList.get(position);
        Picasso.get().load(albumRVModal.imageUrl).into(holder.albumIV);
        holder.albumNameTV.setText(albumRVModal.name);
        holder.albumDetailTV.setText(albumRVModal.artistName);
        //dodavanje click listener za pojedinacni album
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //otvaranje detalja odabranog albuma i prikaz novog activity sa pesmama iz albuma
                Intent i = new Intent(context, AlbumDetailActivity.class);
                //prosledjivanje podatka vezanih za album
                i.putExtra("id", albumRVModal.id);
                i.putExtra("name", albumRVModal.name);
                i.putExtra("img", albumRVModal.imageUrl);
                i.putExtra("artist", albumRVModal.artistName);
                i.putExtra("albumUrl", albumRVModal.external_urls);
                context.startActivity(i);
            }
        });
    }
    //vracanje velicine liste
    @Override
    public int getItemCount() {
        return albumRVModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //kreiranje varijabli za tekst i sliku
        private ImageView albumIV;
        private TextView albumNameTV, albumDetailTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //inicijalizovanje varijabli
            albumIV = itemView.findViewById(R.id.idIVAlbum);
            albumNameTV = itemView.findViewById(R.id.idTVAlbumName);
            albumDetailTV = itemView.findViewById(R.id.idTVALbumDetails);
        }
    }
}

