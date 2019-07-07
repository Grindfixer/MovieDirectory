package com.jwn.moviedirectory.Activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jwn.moviedirectory.Model.Movie;
import com.jwn.moviedirectory.R;
import com.jwn.moviedirectory.Util.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie movie;
    private ImageView movieImage;
    private TextView movieTitle, movieYear, director,
                     actors, category, rating, writers,
                     plot, boxOffice, runtime;

    private RequestQueue queue;
    private String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        queue = Volley.newRequestQueue(this);

        //"movie" parameter is the key from MovieRecyclerViewAdapter intent.putExtra("movie", movie)
        movie = (Movie) getIntent().getSerializableExtra("movie");
        movieId = movie.getImdbId();

        setUpUI();
        getMovieDetails(movieId);
    }

    private void getMovieDetails(String id) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                Constants.URL + id + Constants.URL_RIGHT + Constants.API_KEY, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.has("Ratings")) {
                        //"Ratings" is a node from the JSON response
                        JSONArray ratings = response.getJSONArray("Ratings");

                        String source = null;
                        String value = null;
                        if (ratings.length() > 0) {

                            JSONObject mRatings = ratings.getJSONObject(ratings.length() - 1);
                            source = mRatings.getString("Source");
                            value = mRatings.getString("Value");
                            rating.setText(source + " : " + value   );

                        } else rating.setText(R.string.ratings_not_applicable);
                    }

                    movieTitle.setText(response.getString("Title"));
                    runtime.setText("Runtime: " + response.getString("Runtime"));
                    movieYear.setText("Released: " + response.getString("Released"));
                    director.setText("Director: " + response.getString("Director"));
                    writers.setText("Writers: "  + response.getString("Writer"));
                    actors.setText("Actors: " + response.getString("Actors"));
                    plot.setText("Plot: " + response.getString("Plot"));

                    Picasso.get().load(response.getString("Poster"))
                            .into(movieImage);

                    boxOffice.setText("Box Office: " + response.getString("BoxOffice"));

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }// end onResponse(JSONObject response)

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error:", error.getMessage());
            }
        });

        queue.add(jsonObjectRequest);

    }// end getMovieDetails


    private void setUpUI() {
        movieTitle = findViewById(R.id.movieTitleIDDets);
        movieImage = findViewById(R.id.movieImageIDDets);
        movieYear = findViewById(R.id.movieReleasedIDDets);
        director = findViewById(R.id.directedByDet);
        category = findViewById(R.id.movieCatIDDets);
        rating = findViewById(R.id.movieRatingIDDets);
        writers = findViewById(R.id.writersDet);
        plot = findViewById(R.id.plotDet);
        boxOffice = findViewById(R.id.boxOfficeDet);
        runtime = findViewById(R.id.moviveRuntimeIDDets);
        actors = findViewById(R.id.actorsDet);
    }
}
