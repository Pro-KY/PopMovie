package com.example.proky.popmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.proky.popmovie.data.MoviesContract.MovieEntry;
import com.example.proky.popmovie.data.MoviesContract.CommentEntry;


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

                MovieEntry.COLUMN_COMM_KEY + " INTEGER NOT NULL, " +

                // Set up the comment_id column as a foreign key to comments table.
                " FOREIGN KEY (" + MovieEntry.COLUMN_COMM_KEY + ") REFERENCES " +
                CommentEntry.TABLE_NAME + " (" + CommentEntry._ID + ") ";

        // Create a table to hold movie comments.
        final String SQL_CREATE_COMMENTS_TABLE = "CREATE TABLE " + CommentEntry.TABLE_NAME + " (" +
                CommentEntry._ID + " INTEGER PRIMARY KEY," +

                CommentEntry.COLUMN_AUTHOR + " TEXT, " +
                CommentEntry.COLUMN_COMMENT + " TEXT, " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_COMMENTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CommentEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
