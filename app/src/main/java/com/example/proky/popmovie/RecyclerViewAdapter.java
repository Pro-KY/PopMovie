package com.example.proky.popmovie;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<Review> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mAuthor;
        TextView mReview;

        ViewHolder(View v) {
            super(v);
            mAuthor = (TextView) v.findViewById(R.id.author_text_view);
            mReview = (TextView) v.findViewById(R.id.review_text_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    RecyclerViewAdapter(ArrayList<Review> myDataset) {
        mDataset = myDataset;
        //Log.v("mDataset", Integer.toString(mDataset.size()));
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Review userReview = mDataset.get(position);

        holder.mAuthor.setText(userReview.getmAuthor());
        Log.v("author", userReview.getmAuthor());
        holder.mReview.setText(userReview.getmContent());
        Log.v("content", userReview.getmContent());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
