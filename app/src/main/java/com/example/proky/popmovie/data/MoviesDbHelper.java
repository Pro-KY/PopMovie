package com.example.proky.popmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.proky.popmovie.data.MoviesContract.MovieEntry;
import com.example.proky.popmovie.data.MoviesContract.CommentEntry;

import static android.os.Build.VERSION_CODES.M;
import static com.example.proky.popmovie.data.MoviesContract.MovieEntry.COLUMN_REV_KEY;


/**
 *  Manages a local database for weather data
 */

public class MoviesDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold movies.
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RUNTIME + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +

                // trailers
                MovieEntry.COLUMN_TRAILER_KEY_1 + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TRAILER_KEY_2 + " TEXT, " +

                MovieEntry.COLUMN_SORTING + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_SORTING + " TEXT NOT NULL, " +

                MovieEntry.COLUMN_COMM_KEY + " INTEGER NOT NULL, " +

                // Set up the comment_id column as a foreign key to comments table.
                " FOREIGN KEY (" + MovieEntry.COLUMN_COMM_KEY + ") REFERENCES " +
                CommentEntry.TABLE_NAME + " (" + CommentEntry._ID + ") ";


    }
}
