package hu.dushu.developers.wordbook.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by renfeng on 12/31/14.
 */
public class WordProvider extends ContentProvider {

    private static final String LOG_TAG = WordProvider.class.getSimpleName();

    private WordDbHelper helper;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int WORD = 100;
    private static final int WORD_ID = 101;
    private static final int WORD_LITERAL = 102;
//    private static final int WORD_NEW = 103;

    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WordContract.CONTENT_AUTHORITY;

        /*
         * TODO see if it requires a word lookup
         */
        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, WordContract.PATH_WORD, WORD);
        matcher.addURI(authority, WordContract.PATH_WORD + "/#", WORD_ID);
        matcher.addURI(authority, WordContract.PATH_WORD + "/*", WORD_LITERAL);
//        matcher.addURI(authority, WordContract.PATH_WORD + "/new", WORD_NEW);

        return matcher;
    }

    @Override
    public boolean onCreate() {

        setHelper(new WordDbHelper(getContext()));

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        switch (sUriMatcher.match(uri)) {
            case WORD: {
                return helper.getReadableDatabase().query(
                        WordContract.WordEntity.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }
            case WORD_ID: {
                long id = ContentUris.parseId(uri);
                return helper.getReadableDatabase().query(
                        WordContract.WordEntity.TABLE_NAME,
                        projection,
                        WordContract.WordEntity._ID + " = ?",
                        new String[]{id + ""},
                        null,
                        null,
                        sortOrder);
            }
            case WORD_LITERAL: {
                String word = uri.getPathSegments().get(1);
                return helper.getReadableDatabase().query(
                        WordContract.WordEntity.TABLE_NAME,
                        projection,
                        WordContract.WordEntity.COLUMN_WORD + " = ?",
                        new String[]{word},
                        null,
                        null,
                        sortOrder);
            }
//            case WORD_NEW: {
//                return helper.getReadableDatabase().query(
//                        WordContract.WordEntity.TABLE_NAME,
//                        projection,
//                        WordContract.WordEntity.COLUMN_DEFINITION + " IS NULL",
//                        null,
//                        null,
//                        null,
//                        sortOrder);
//            }
        }

        return null;
    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case WORD: {
                return WordContract.WordEntity.CONTENT_TYPE;
            }
            case WORD_ID: {
                return WordContract.WordEntity.CONTENT_ITEM_TYPE;
            }
            case WORD_LITERAL: {
                return WordContract.WordEntity.CONTENT_ITEM_TYPE;
            }
        }

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = helper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case WORD: {
                long _id = db.insert(WordContract.WordEntity.TABLE_NAME, null, values);
                if (_id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return WordContract.WordEntity.buildWordUri(_id);
//                } else {
//                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
            }
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = helper.getWritableDatabase();
        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case WORD:
                rowsDeleted = db.delete(
                        WordContract.WordEntity.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /*
         * TODO remove selection == null
         */
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = helper.getWritableDatabase();
        int rowsUpdated;
        switch (sUriMatcher.match(uri)) {
            case WORD:
                rowsUpdated = db.update(
                        WordContract.WordEntity.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /*
         * TODO remove selection == null
         */
        // Because a null updates all rows
        if (selection == null || rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    public WordDbHelper getHelper() {
        return helper;
    }

    public void setHelper(WordDbHelper helper) {
        this.helper = helper;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = helper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case WORD:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(WordContract.WordEntity.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                Log.d(LOG_TAG, "number of word records inserted: " + returnCount);
                return returnCount;
        }

        return super.bulkInsert(uri, values);
    }
}
