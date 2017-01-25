package com.example.proky.popmovie;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import com.squareup.picasso.Picasso;

class FilmsAdapter extends ArrayAdapter<Film> {

    private final String LOG_TAG = FilmsAdapter.class.getSimpleName();
    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     */
    // List<FilmPosters> androidAnimals
    FilmsAdapter(Activity context, List<Film> films) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, films);
        Log.v("FilmsAdapter", "Completed!");
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the FilmPosters object from the ArrayAdapter at the appropriate position
        Film film = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_view_items, parent, false);
        }

        ImageView filmPosterView = (ImageView) convertView.findViewById(R.id.imageView);

        try {
            //Loading Image from URL
            Picasso.with(getContext())
                    .load(film.getPosterPath())
                    .placeholder(R.drawable.placeholder)   // optional
                    .error(R.drawable.error)               // optional
                    .into(filmPosterView);
        } catch(NullPointerException e) {
            Log.e(LOG_TAG, "Error getting poster path", e);
        }

        return convertView;
    }
}
