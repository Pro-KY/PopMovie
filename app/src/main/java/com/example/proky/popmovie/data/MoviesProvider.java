package com.example.proky.popmovie.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import static com.example.proky.popmovie.data.MoviesContract.MovieEntry.buildMoviesUri;


public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int COMMENTS = 200;

    private static final SQLiteQueryBuilder sMovieWithCommentQueryBuilder;

    // Static constructor of the class
    static {
        sMovieWithCommentQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //movies INNER JOIN comments ON movies.comment_id = comments._id
        sMovieWithCommentQueryBuilder.setTables(
                MoviesContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.CommentEntry.TABLE_NAME +
                        " ON " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry.COLUMN_COMM_KEY +
                        " = " + MoviesContract.CommentEntry.TABLE_NAME +
                        "." + MoviesContract.CommentEntry._ID);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    // This UriMatcher will match each URI to the MOVIES and COMMENTS integer constants defined above.
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);

        matcher.addURI(authority, MoviesContract.PATH_COMMENTS, COMMENTS);

        // 3) Return the new matcher!
        return matcher;
    }

    // Takes content provider uri's and query database content accordingly
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movies"
            case MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "comments"
            case COMMENTS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.CommentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

//
//        //  ------------- FOR DEBUGGING PURPOSES -------------------
//        if(retCursor.moveToFirst()) {
//            String[] columnNames = retCursor.getColumnNames();
//
//            Object value = null;
//
//            do{
//                int rowNumber = retCursor.getPosition();
//                Log.i("----- rowNumber -----", Integer.toString(rowNumber) + " -------------------");
//                for(String colName : columnNames) {
//                    int idIndex = retCursor.getColumnIndex(colName);
//
//                    switch(retCursor.getType(idIndex)) {
//                        case Cursor.FIELD_TYPE_INTEGER: {
//                            value = retCursor.getInt(idIndex);
//                            break;
//                        }
//                        case Cursor.FIELD_TYPE_STRING: {
//                            value = retCursor.getString(idIndex);
//                            break;
//                        }
//                        case Cursor.FIELD_TYPE_FLOAT: {
//                            value = retCursor.getFloat(idIndex);
//                            break;
//                        }
//                    }
//                    Log.i(colName , value.toString());
//                }
//                Log.i("-------", "end of row" + "--------");
//            } while(retCursor.moveToNext());
//        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    // Insert movies and comments data into their corresponding db
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = buildMoviesUri(_id);
                    Log.d("insert_movies_uri", returnUri.toString());
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case COMMENTS: {
                long _id = db.insert(MoviesContract.CommentEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = MoviesContract.CommentEntry.buildCommentsUri(_id);
                    Log.d("insert_comments_uri", returnUri.toString());
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        try {
            // notify all of the registered observers
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return returnUri;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES: {
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }

            case COMMENTS: {
                rowsUpdated = db.update(MoviesContract.CommentEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify the content resolver of change if we actually have updated some rows
        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.
        final int match = sUriMatcher.match(uri);

        // this makes delete all rows return the number of rows deleted
        if(selection == null) selection = "1";
        switch (match) {
            case MOVIES: {
                rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case COMMENTS: {
                rowsDeleted = db.delete(MoviesContract.CommentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the content resolver of change if we actually have deleted some rows
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return the actual rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case COMMENTS:
                return MoviesContract.CommentEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
