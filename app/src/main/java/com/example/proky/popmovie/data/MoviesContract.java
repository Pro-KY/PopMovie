package com.example.proky.popmovie.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movies database.
 */
public class MoviesContract {

    // The "Content authority" is a name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.example.proky.popmovie";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/movies/ is a valid path for
    // looking at movies data.
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_COMMENTS = "comments";

    /**
     *  Defines the table contents of the movies table
     */
    public static final class MovieEntry implements BaseColumns {
        // Represents the base location for each table
        // "content://com.example.proky.popmovie/movies"
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        // URI containing a Cursor of a muiltiple items.
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        // URI containing a Cursor of a single item.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "movies";

        // movie id provided by API, stored as integer
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Column with the foreign key into the comment table.
        public static final String COLUMN_COMM_KEY = "comment_id";

        // movie title provided by API, stored as string
        public static final String COLUMN_TITLE = "title";

        // The location setting string is what will be sent to www.themoviedb.org/
        // as the location query, stored as string.
        public static final String COLUMN_SORTING = "sorting";

        // Poster path provided by API, stored as string
        public static final String COLUMN_POSTER = "poster_path";

        // film overview provided by API, stored as string
        public static final String COLUMN_OVERVIEW = "overview";

        // movie release date provided by API, stored as string
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // movie duration provided by API, stored as integer
        public static final String COLUMN_RUNTIME = "runtime";

        // movie rating provided by API, stored as double
        public static final String COLUMN_RATING = "vote_average";

        // part of link to a movie trailer provided by API, stored as string
        public static final String COLUMN_TRAILER_KEY_1 = "first_trailer_key";

        // part of link to a movie trailer provided by API, stored as string
        public static final String COLUMN_TRAILER_KEY_2 = "second_trailer_key";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    /**
     *  Defines the table contents of the comments table
     */
    public static final class CommentEntry implements BaseColumns {
        // Represents the base location for each table
        // "content://com.example.proky.popmovie/comments"
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENTS).build();

        // URI containing a Cursor of a muiltiple items.
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMMENTS;
        // URI containing a Cursor of a single item.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMMENTS;

        public static final String TABLE_NAME = "comments";

        // movie author nickname provided by API, stored as string
        public static final String COLUMN_AUTHOR = "author";
        // movie comment provided by author, stored as string
        public static final String COLUMN_COMMENT = "comment";

        public static Uri buildCommentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
