package com.example.proky.popmovie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.example.proky.popmovie.MainActivity.mShareActionProvider;

public class MainActivityFragment extends Fragment {

    private FilmsAdapter mPostersAdapter;
    private ProgressBar mLoadingIcon;
    SwipeRefreshLayout swipeLayout;
    private TreeMap<String, Double> mMovieTitles;
    String moviesAndRating;

    public MainActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mLoadingIcon = (ProgressBar)rootView.findViewById(R.id.loadingCircle) ;
        //postersAdapter = new FilmsAdapter(getActivity(), Arrays.asList(new Film[0]));
        mPostersAdapter = new FilmsAdapter(getActivity(), new ArrayList<Film>());

        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.simpleGridView);
        gridView.setAdapter(mPostersAdapter);

        // Go to DetailActivity when clicked on movie poster
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Film film = mPostersAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);

                // I think that in this place I should use Parcelable conception
                try {
                    intent.putExtra("id", film.getId());
                    startActivity(intent);
                } catch(NullPointerException e) {
                    Log.e(getActivity().getLocalClassName(), "one of film obj attr is null", e);
                }
            }
        });

        // Swipe to Refresh
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefresh);
        //swipeLayout.setRefreshing( false );

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000);

                if(mPostersAdapter.isEmpty()) {
                    onStart();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(!isOnline()) {
            Toast.makeText(getContext(),"There is no internet connection, please, turn on your Wi-Fi" +
                            "and try again",
                    Toast.LENGTH_LONG).show();

            mLoadingIcon.setVisibility(View.INVISIBLE);
            //swipeLayout.setEnabled(false);
        } else {
            updateFilmInfo();
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //Log.v("createShareIntent", moviesAndRating);
        shareIntent.putExtra(Intent.EXTRA_TEXT, moviesAndRating);

        return shareIntent;
    }

    private void updateFilmInfo() {
        FetchFilmsTask filmTask = new FetchFilmsTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Get the current value established in sorting method in preferences
        String sortingMethod = prefs.getString(getString(R.string.pref_sorting_key),
                getString(R.string.pref_sort_value_default));

        // Get the current value established in language preferences
        String language = prefs.getString(getString(R.string.pref_language_key),
                getString(R.string.pref_lang_value_default));
        //Log.v("updateFilmInfo - lang", language);

        filmTask.execute(sortingMethod, language);
    }

    // Checking Internet connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // String - JSON data in string format;
    public class FetchFilmsTask extends AsyncTask<String, Void, Film[]> {

        private final String LOG_TAG = FetchFilmsTask.class.getSimpleName();

        // Parsing String JSON response and pulling out film title, poster and other attributes
        private Film[] getFilmsDataFromJson(String filmsJsonStr)
                throws JSONException {
            // using the format "title - poster_path"
            String movieTitle;
            double movieRating;
            String posterPath;
            int id;

            // names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String POSTER = "poster_path";
            final String TITLE = "title";
            final String RATING = "vote_average";
            final String ID = "id";

            JSONObject filmsJson = new JSONObject(filmsJsonStr);
            JSONArray filmsArray = filmsJson.getJSONArray(RESULTS);
            int filmsArrayLength = filmsArray.length();

            Film[] films = new Film[filmsArrayLength];
            mMovieTitles = new TreeMap<>();

            String baseImageUrl = "http://image.tmdb.org/t/p/w185";

            for(int i = 0; i < filmsArrayLength; i++) {
                // Get the JSON object representing one film
                JSONObject film = filmsArray.getJSONObject(i);

                movieTitle = film.getString(TITLE);
                movieRating = film.getDouble(RATING);
                id = film.getInt(ID);
                posterPath = film.getString(POSTER);

                // Adding elements to TreeMap
                mMovieTitles.put(movieTitle, movieRating);
                // Adding elements to array of Film objects
                films[i] = new Film(id, baseImageUrl + posterPath);
            }
            return films;
        }

        @Override
        // params[0] - sorting method
        protected Film[] doInBackground(String... params) {

            // If there's no parameters - return null
            if(params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string
            String filmsJsonStr = null;
            // API key which is needed to get response from the server
            String api_key = "";
            //String lang_russian = "ru";
            //String lang_english = "en-US";

            try {
                // Construct the URL for the TheMovieDB query
                final String FILMS_BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0];
                final String API_KEY_PARAM = "api_key";
                final String LANGUAGE = "language";

                Uri builtUri = Uri.parse(FILMS_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, api_key)
                        .appendQueryParameter(LANGUAGE, params[1])
                        .build();

                //Log.v("Url:", builtUri.toString());
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

                filmsJsonStr = builder.toString();

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
                return getFilmsDataFromJson(filmsJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        // here passed executed result from doInBackground method
        @Override
        protected void onPostExecute(Film[] films) {
            mLoadingIcon.setVisibility(View.GONE);

            if(films != null) {
                mPostersAdapter.clear();

                for(Film film : films) {
                    mPostersAdapter.add(film);
                }
            }

            //Log.v("onPostExecute", getMovieTitles());
            moviesAndRating = getMovieTitles();

            // Call to update the share intent
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    public String getMovieTitles() {
        StringBuilder builder = new StringBuilder();

        // Adding content in builder using Iterator
        Set<Map.Entry<String, Double>> set = entriesSortedByValues(mMovieTitles);

        for(Map.Entry<String, Double> entry : set) {
            String key_title = entry.getKey();
            Double value_rating = entry.getValue();
            builder.append(key_title).append(" - ").append(value_rating).append("\n");
        }

        return builder.toString();
    }

    // Sorting collection in descending order by values
    static <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {

        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e2.getValue().compareTo(e1.getValue());
                        return res != 0 ? res : 1;
                    }
                }
        );

        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
