package com.example.musicapp;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
	//kreiranje varijabli
	String searchQuery = "";
	private EditText searchEdt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		//inicijalizovanje varijabli
		searchEdt = findViewById(R.id.idEdtSearch);
		searchQuery = getIntent().getStringExtra("searchQuery");
		searchEdt.setText(searchQuery);
		//dodavanje action listener za search edit text
		searchEdt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					// on below line calling method to get tracks.
					//pozivanje metode za vracanje numera
					getTracks(searchEdt.getText().toString());
					return true;
				}
				return false;
			}
		});
		//vracanje numera
		getTracks(searchQuery);

	}

	//metod za dobijanje tokena
	private String getToken() {
		SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
		return sh.getString("token", "Not Found");
	}


	private void getTracks(String searchQuery) {
		//kreiranje i inicijalizovanje varijabli za reciklirani pogled, listu i adapter
		RecyclerView songsRV = findViewById(R.id.idRVSongs);
		ArrayList<TrackRVModal> trackRVModals = new ArrayList<>();
		TrackRVAdapter trackRVAdapter = new TrackRVAdapter(trackRVModals, this);
		songsRV.setAdapter(trackRVAdapter);
		//kreiranje odabranog spotify url-a
		String url = "https://api.spotify.com/v1/search?q=" + searchQuery + "&type=track";
		RequestQueue queue = Volley.newRequestQueue(SearchActivity.this);
		//kreiranje json objekt zahteva
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject trackObj = response.getJSONObject("tracks");
					JSONArray itemsArray = trackObj.getJSONArray("items");
					for (int i = 0; i < itemsArray.length(); i++) {
						JSONObject itemObj = itemsArray.getJSONObject(i);
						String trackName = itemObj.getString("name");
						String trackArtist = itemObj.getJSONArray("artists").getJSONObject(0).getString("name");
						String trackID = itemObj.getString("id");
						//dodavanje podatak u listu
						trackRVModals.add(new TrackRVModal(trackName, trackArtist, trackID));
					}
					//obavestavanje adaptera o izmeni
					trackRVAdapter.notifyDataSetChanged();

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(SearchActivity.this, "Fail to get data : " + error, Toast.LENGTH_SHORT).show();
			}
		}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				//dodavanje naslova
				HashMap<String, String> headers = new HashMap<>();
				headers.put("Authorization", getToken());
				headers.put("Accept", "application/json");
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};
		// dodavanje json zahteva u red
		queue.add(jsonObjectRequest);

	}
}
