package com.example.proky.popmovie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Intent.ACTION_VIEW;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Set action bar title according to appropriate selected language
        try {
            getSupportActionBar().setTitle(R.string.title_activity_detail);
        } catch(NullPointerException e) {
            e.printStackTrace();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_layout, new DetailFragment())
                    .commit();
        }
    }

    public static class DetailFragment extends Fragment {
        public DetailFragment() {}

        String title, posterPath, releaseDate, overview, firstTrailerKey, secondTrailerKey;
        double rating;
        int runtime;

        View rootView;
        TextView titleView, releaseDateView, runtimeView, ratingView, overviewView, noReviewsTextView;
        ImageView posterView;
        ImageButton ImageButtonPlay1, ImageButtonPlay2;
        ProgressBar loadingIcon;
        LinearLayout childLinearLayout;

        private RecyclerView mRecyclerView;
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;
        private ArrayList<Review> mReviewsArrayList;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Log.v("onCreateView", "I've run!");

            rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // Initiate view's
            initViews();

            // perform click event on ImageButton's
            ImageButtonPlay1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getContext(),"trailer1",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ACTION_VIEW);
                    intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + firstTrailerKey));

                    // Verify that the intent will resolve to an activity
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });

            ImageButtonPlay2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(secondTrailerKey != null) {
                        Intent intent = new Intent(ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + secondTrailerKey));
                        Log.v("trailer2_url","http://www.youtube.com/watch?v=" + secondTrailerKey);

                        // Verify that the intent will resolve to an activity
                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getContext(),"There is no second trailer =(",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            return rootView;
        }

        public void onStart() {
            Log.v("onStart", "I've run!");
            super.onStart();
            getFilmData();
        }

        private void getFilmData() {
            FetchFilmsDetailsTask filmTask = new FetchFilmsDetailsTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            // pass movie id to "doInBackground" method
            Log.v("movie id", Integer.toString(retrieveIdFromIntent()));

            // Get the current value established in language preferences
            String language = prefs.getString(getString(R.string.pref_language_key),
                    getString(R.string.pref_lang_value_default));
            filmTask.execute(Integer.toString(retrieveIdFromIntent()), language);
        }

        public class FetchFilmsDetailsTask extends AsyncTask<String, Void, Void> {

            private final String LOG_TAG = FetchFilmsDetailsTask.class.getSimpleName();

            // Parsing String JSON response and pulling out film title, poster, etc. and
            // assigning this values to DetailFragment fields.
            private ArrayList<Review> getFilmDataFromJson(String filmJsonStr)
                    throws JSONException {

                // names of the JSON objects that need to be extracted.
                final String TITLE = "title";
                final String POSTER = "poster_path"; // poster_path
                final String RELEASE_DATE = "release_date";
                final String RUNTIME = "runtime";
                final String RATING = "vote_average";
                final String OVERVIEW = "overview";
                final String VIDEO = "videos";
                final String RESULTS = "results";
                final String TRAILER_KEY = "key";
                final String REVIEWS = "reviews";
                final String TOTAL_REVIEWS = "total_results";
                final String REVIEW_AUTHOR = "author";
                final String REVIEW_CONTENT = "content";

                JSONObject movieJson = new JSONObject(filmJsonStr);

                String baseImageUrl = "http://image.tmdb.org/t/p/w185";

                // get film title
                title = movieJson.getString(TITLE);
                Log.v("title", title);
                // get poster path
                posterPath = baseImageUrl.concat(movieJson.getString(POSTER));
                Log.v("posterPath", posterPath);
                // get movie release date
                releaseDate = movieJson.getString(RELEASE_DATE);
                Log.v("releaseDate", releaseDate);
                // get movie runtime
                runtime = movieJson.getInt(RUNTIME);
                Log.v("runtime", Integer.toString(runtime));
                // get film rating
                rating = movieJson.getDouble(RATING);
                Log.v("rating", Double.toString(rating));
                // get film overview
                overview = movieJson.getString(OVERVIEW);
                Log.v("overview", overview);

                JSONObject video = movieJson.getJSONObject(VIDEO);
                //Log.v("videoJson", video);
                JSONArray moviesArray = video.getJSONArray(RESULTS);

                // first trailer
                JSONObject trailer1 = moviesArray.getJSONObject(0);
                firstTrailerKey = trailer1.getString(TRAILER_KEY);
                Log.v("firstTrailerKey", firstTrailerKey);

                // Retrieve "reviews" from json
                JSONObject reviews = movieJson.getJSONObject(REVIEWS);
                int total_reviews = reviews.getInt(TOTAL_REVIEWS);
                Log.v("total_reviews", Integer.toString(total_reviews));
                JSONArray reviewsJsonArray = reviews.getJSONArray(RESULTS);

                //mReviewsArrayList = new ArrayList<>();

                for(int i=0; i<total_reviews; i++) {
                    JSONObject humanReview = reviewsJsonArray.getJSONObject(i);
                    String reviewAuthor = humanReview.getString(REVIEW_AUTHOR);
                    Log.v("reviewAuthor", reviewAuthor);

                    String reviewContent = humanReview.getString(REVIEW_CONTENT);
                    Log.v("reviewContent", reviewContent);

                    Review filmReview = new Review(reviewAuthor, reviewContent);
                    mReviewsArrayList.add(i, filmReview);
                }

                Log.v("mReviewsArrayList", Integer.toString(mReviewsArrayList.size()));

                if(moviesArray.length() >= 2) {
                    // second trailer
                    JSONObject trailer2 = moviesArray.getJSONObject(1);
                    secondTrailerKey = trailer2.getString(TRAILER_KEY);
                    Log.v("secondTrailerKey", secondTrailerKey);
                } else {
                    secondTrailerKey = null;
                }

               //return mReviewsArrayList;
                return null;
            }

            @Override
            // send http request in order to get respond in JSON format and translate into String
            protected Void doInBackground(String... params) {
                // params[0] - movie id

                // If there's no parameters - return null
                if(params.length == 0) {
                    return null;
                }

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string
                String filmJsonStr = null;
                // API key which is needed to get response from the server
                String api_key = "0b032967d90ed255bad3d6c1a9ec177b";
                //String lang_russ = "ru";
                String sub_request_values = "videos";
                String reviews = "\u002Creviews";

                try {
                    // Construct the URL for the TheMovieDB query
                    final String FILMS_BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0];
                    final String API_KEY_PARAM = "api_key";
                    final String SUB_REQUEST_PARAM = "append_to_response";
                    final String LANGUAGE = "language";

                    String decodedString = URLDecoder.decode(reviews, "UTF-8");

                    Uri builtUri = Uri.parse(FILMS_BASE_URL).buildUpon()
                            .appendQueryParameter(API_KEY_PARAM, api_key)
                            .appendQueryParameter(SUB_REQUEST_PARAM, sub_request_values.concat(decodedString))
                            .appendQueryParameter(LANGUAGE, params[1])
                            .build();
                    // .appendQueryParameter(LANGUAGE, lang_russ)

                    Log.v("Url:", builtUri.toString());
                    URL url = new URL(builtUri.toString());

                    // Create the request to TheMovieDB, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder builder = new StringBuilder();

                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        builder.append(line).append("\n");
                    }

                    // If Stream was empty. No point in parsing.
                    if (builder.length() == 0) {
                        return null;
                    }

                    filmJsonStr = builder.toString();
                    Log.v("filmJsonStr", filmJsonStr);

                } catch(IOException e) {
                    Log.e(LOG_TAG, "Error", e);
                    return null;
                } finally {
                    if(urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if(reader != null) {
                        try {
                            reader.close();
                        } catch(IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                try {
                    getFilmDataFromJson(filmJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }

            // here passed executed result from doInBackground method
            @Override
            protected void onPostExecute(Void p) {
                // Remove loading circle when data set in all views
                loadingIcon.setVisibility(View.INVISIBLE);
                childLinearLayout.setVisibility(View.VISIBLE);
                setDataIntoViews();
            }
        }

        // Retrieving data from Intent and assigning them to appropriate fields
        private int retrieveIdFromIntent() {
            Intent intent = getActivity().getIntent();
            int filmId = 0; // just init value

            if (intent != null) {
                filmId = intent.getIntExtra("id", 0);
            }
            return filmId;
        }

        private static String getMonthShortName(int monthNumber) {
            String monthName = "";

            if (monthNumber >= 0 && monthNumber < 12)
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, monthNumber-1);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM", Locale.US);
                    //simpleDateFormat.setCalendar(calendar);
                    monthName = simpleDateFormat.format(calendar.getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return monthName;
        }

        // return formatted release film date as "monthName, day, year"
        private String getFormattedRealeaseDate() {
            String releaseYear = releaseDate.substring(0, 4);
            String releaseDayOfMonth = releaseDate.substring(8);

            // month number with zero before number representation (01, 02, etc)
            String monthNumberStr = releaseDate.substring(5, 7);
            // int representation of month
            int monthNumber = Integer.valueOf(monthNumberStr);

            // Short name of month
            String monthShortName = getMonthShortName(monthNumber);

            return String.format("%1s %2s, %3s", monthShortName,
                    releaseDayOfMonth, releaseYear  );
        }

        // Initialization view's
        private void initViews() {

            childLinearLayout = (LinearLayout) rootView.findViewById(R.id.childLinearLayout);
            childLinearLayout.setVisibility(View.INVISIBLE);

            // Progress bar
            loadingIcon = (ProgressBar) rootView.findViewById(R.id.loadingCircleDetail);

            titleView = (TextView)rootView.findViewById(R.id.film_title);
            posterView = (ImageView)rootView.findViewById(R.id.film_poster);
            releaseDateView = (TextView)rootView.findViewById(R.id.release_date);
            runtimeView = (TextView)rootView.findViewById(R.id.duration);
            ratingView = (TextView)rootView.findViewById(R.id.film_rating);
            overviewView = (TextView)rootView.findViewById(R.id.film_overview);
            noReviewsTextView = (TextView)rootView.findViewById(R.id.no_reviews_text);

            ImageButtonPlay1 = (ImageButton)rootView.findViewById(R.id.playButton_1);
            ImageButtonPlay2 = (ImageButton)rootView.findViewById(R.id.playButton_2);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            mReviewsArrayList = new ArrayList<>();
            mAdapter = new RecyclerViewAdapter(mReviewsArrayList);
            mRecyclerView.setAdapter(mAdapter);
        }

        // Setting Retrieved data into Views
        private void setDataIntoViews() {
            titleView.setText(title);

            Picasso.with(getContext())
                    .load(posterPath)
                    .placeholder(R.drawable.placeholder)   // optional
                    .error(R.drawable.error)               // optional
                    .into(posterView);

            // change release date representation and rating precise before assigning to views
            String formattedRating = String.format(Locale.getDefault(), "%1$.1f/10.0", rating);
            String formattedReleaseDate = getFormattedRealeaseDate();

            String formattedDuration = String.format(Locale.getDefault(), "%d min", runtime);

            Resources res = getResources();
            String releaseDate = String.format(res.getString(R.string.release_date),
                    formattedReleaseDate);
            String rating = String.format(res.getString(R.string.rating), formattedRating);
            String duration = String.format(res.getString(R.string.duration), formattedDuration);

            // Assigning data to views
            releaseDateView.setText(releaseDate);
            runtimeView.setText(duration);
            ratingView.setText(rating);
            overviewView.setText(overview);

            Log.v("mReviewsArrayList", Integer.toString(mReviewsArrayList.size()));

            if(mReviewsArrayList.size() == 0) {
                noReviewsTextView.setText(R.string.no_reviews_message);
            } else {
                noReviewsTextView.setHeight(0);
                mRecyclerView.setAdapter(mAdapter);
            }
        }


    }
}
