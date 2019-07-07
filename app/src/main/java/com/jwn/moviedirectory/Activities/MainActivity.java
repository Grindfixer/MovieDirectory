package com.jwn.moviedirectory.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jwn.moviedirectory.Data.MovieRecyclerViewAdapter;
import com.jwn.moviedirectory.Model.Movie;
import com.jwn.moviedirectory.R;
import com.jwn.moviedirectory.Util.Constants;
import com.jwn.moviedirectory.Util.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieRecyclerViewAdapter movieRecyclerViewAdapter;
    private List<Movie> movieList;
    private RequestQueue queue;
    private JsonObjectRequest jsonObjectRequest;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queue = Volley.newRequestQueue(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showInputDialog();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Prefs prefs = new Prefs(MainActivity.this);
        String search = prefs.getSearch();

        movieList = new ArrayList<>();


       // getMovies(search);
        movieList = getMovies(search);

        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(this,movieList);
        recyclerView.setAdapter(movieRecyclerViewAdapter);
        movieRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action automatically
        // handles clicks on the Home/Up button as long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_search) {
            showInputDialog();
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showInputDialog() {
        alertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_view,null);
        final EditText newSearchEdt = view.findViewById(R.id.searchEdt);
        Button submitButton = view.findViewById(R.id.submitButton);

        //set to the view that was just created
        alertDialogBuilder.setView(view);
        dialog = alertDialogBuilder.create();
        dialog.show();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // take user input, save into sharedPreference, update recylcerViewer
                Prefs prefs = new Prefs(MainActivity.this);

                if (!newSearchEdt.getText().toString().isEmpty()) {
                    String search = newSearchEdt.getText().toString();
                    prefs.setSearch(search);
                    movieList.clear();

                    getMovies(search);

                }
                dialog.dismiss();
            }
        });

    }


    // get movies
    public List<Movie> getMovies(String searchTerm) {
        // clear & repopulate list every time its called
        movieList.clear();

        //build the JSON request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                Constants.URL_LEFT + searchTerm + Constants.URL_RIGHT + Constants.API_KEY, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    JSONArray moviesArray = response.getJSONArray("Search");

                    for (int i = 0; i < moviesArray.length(); i++) {

                        JSONObject movieObj = moviesArray.getJSONObject(i);

                        Movie movie = new Movie();
                        movie.setTitle(movieObj.getString("Title"));
                        movie.setYear("Year Released: " + movieObj.getString("Year"));
                        movie.setMovieType("Type: " + movieObj.getString("Type"));
                        movie.setPoster(movieObj.getString("Poster"));
                        movie.setImdbId(movieObj.getString("imdbID"));


                        //Log.d("Movies: ", movie.getTitle());

                        movieList.add(movie);

                    }

                    // Very important!! Otherwise nothing will be displayed.
                    movieRecyclerViewAdapter.notifyDataSetChanged();//Important!!


                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);

        return movieList;
    }


}// end MainActivity
