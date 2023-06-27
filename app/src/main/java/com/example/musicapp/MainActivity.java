package com.example.musicapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //pozivanje teme
        setTheme(R.style.Theme_MusicApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //pozivanje metode za prikaz albuma u trendu
        initializeTrendingAlbumsRV();
        //pozivanje metoda za pretragu albuma
        initializeSearchView();
    }

    // metod koji se koristi za inicijalizaciju pretrage
    private void initializeSearchView() {
        //inicijalizacija teksta za pretragu
        EditText searchEdt = findViewById(R.id.idEdtSearch);
        searchEdt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //pozivanje metode za pretragu albuma
                    searchTracks(searchEdt.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    //metod za pretragu albuma
    private void searchTracks(String searchQuery) {
        //prikaz rezultata pretrage u search activity
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        i.putExtra("searchQuery", searchQuery);
        startActivity(i);
    }

    //metod za dobijanje tokena
    private String getToken() {
        //dobijanje tokena iz shared prefs (lokalno cuvanje)
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return sh.getString("token", "Not Found");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //pozivanje metode za generisanje tokena
        generateToken();
    }

    //metoda za generisanje tokena
    private void generateToken() {
        // URL za generisanje pristupnog tokena
        String url = "https://accounts.spotify.com/api/token?grant_type=client_credentials";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // Klijentski ID i klijentski tajni ključ
        final String clientId = "853de8f07b344f07b4e9728cf7c2b4e6";
        final String clientSecret = "724b06cd5d1648e48397d3d16ddc5954";

        // Slanje zahteva za generisanje pristupnog tokena
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // Dobijanje pristupnog tokena iz odgovora
                    String accessToken = jsonObject.getString("access_token");
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("token", "Bearer " + accessToken);
                    myEdit.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Obrada grešaka
                Toast.makeText(MainActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // Kodiranje klijentskog ID-a i tajnog ključa u Base64 format
                String credentials = clientId + ":" + clientSecret;
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                // Postavljanje zaglavlja
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                headers.put("Content-Type", "application/x-www-form-urlencoded");

                return headers;
            }
        };
        // Dodavanje zahteva u red zahteva
        queue.add(request);
    }
    //metoda za inicijalizaciju albuma u trendu
    private void initializeTrendingAlbumsRV() {
        //kreiranj liste, inicijalizacija adatera i postavljanje recikliranog pogleda
        RecyclerView albumsRV = findViewById(R.id.idRVTrendingAlbums);
        ArrayList<AlbumRVModal> albumRVModalArrayList = new ArrayList<>();
        AlbumRVAdapter albumRVAdapter = new AlbumRVAdapter(albumRVModalArrayList, this);
        albumsRV.setAdapter(albumRVAdapter);
        // on below line creating a variable for url
        //varijabla za odabrani spotify url
        String url = "https://api.spotify.com/v1/albums?ids=1P4eCx5b11Tfmi4s1GmWmQ%2C2SsEtiB6yJYn8hRRAmtVda%2C7hhxms8KCwlQCWffIJpN9b%2C3umvKIjsD484pa9pCyPK2x%2C3OHC6XD29wXWADtAOP2geV%2C3RZxrS2dDZlbsYtMRM89v8%2C24C47633GRlozws7WBth7t";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        //kreiranje json objekta za parsiranje json podataka
        JsonObjectRequest albumObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray albumArray = response.getJSONArray("albums");
                    for (int i = 0; i < albumArray.length(); i++) {
                        JSONObject albumObj = albumArray.getJSONObject(i);
                        String album_type = albumObj.getString("album_type");
                        String artistName = albumObj.getJSONArray("artists").getJSONObject(0).getString("name");
                        String external_ids = albumObj.getJSONObject("external_ids").getString("upc");
                        String external_urls = albumObj.getJSONObject("external_urls").getString("spotify");
                        String href = albumObj.getString("href");
                        String id = albumObj.getString("id");
                        String imgUrl = albumObj.getJSONArray("images").getJSONObject(1).getString("url");
                        String label = albumObj.getString("label");
                        String name = albumObj.getString("name");
                        int popularity = albumObj.getInt("popularity");
                        String release_date = albumObj.getString("release_date");
                        int total_tracks = albumObj.getInt("total_tracks");
                        String type = albumObj.getString("type");
                        //dodavanje podataka u listu
                        albumRVModalArrayList.add(new AlbumRVModal(album_type, artistName, external_ids, external_urls, href, id, imgUrl, label, name, popularity, release_date, total_tracks, type));
                    }
                    albumRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get data : " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //prosledjivanje naslova
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        //dodavanje zahteva u red
        queue.add(albumObjReq);
    }
}
