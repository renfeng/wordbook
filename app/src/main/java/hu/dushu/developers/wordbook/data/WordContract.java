package hu.dushu.developers.wordbook.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by renfeng on 12/30/14.
 */
public class WordContract {

    /*
     * TODO naming convention, wordBook or word_book?
     */
    public static final String CONTENT_AUTHORITY = "hu.dushu.developers.wordbook";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WORD = "word";

    /*
     * TODO refine the date precision to seconds
     */
    public static final String DATE_FORMAT = "yyyyMMdd";

    public static final class WordEntity implements BaseColumns {

        public static final String TABLE_NAME = "word";

        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_DEFINITION = "definition";
        public static final String COLUMN_VIEW_COUNT = "view_count";
        public static final String COLUMN_LAST_SEEN = "last_seen";

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WORD;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WORD;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORD).build();

        public static Uri buildWordUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWithWord(String word) {
            return CONTENT_URI.buildUpon().appendPath(word).build();
        }

    }

    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     *
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    /*
    4b - Finishing the FetchWeatherTask
    https://www.udacity.com/course/viewer#!/c-ud853/l-1576308909/m-1675098569
    */
    public static String getDbDateString(Date date) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     *
     * @param dateText the input date string
     * @return the Date object
     */
    /*
    4b - Finishing the FetchWeatherTask
    https://www.udacity.com/course/viewer#!/c-ud853/l-1576308909/m-1675098569
    */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long add(String word, Context context) {

        ContentValues testValues = new ContentValues();
        testValues.put(WordEntity.COLUMN_WORD, word);
        testValues.put(WordEntity.COLUMN_VIEW_COUNT, 0);
        testValues.put(WordEntity.COLUMN_LAST_SEEN,
                getDbDateString(new Date()));

        Uri locationUri = context.getContentResolver().insert(
                WordEntity.CONTENT_URI, testValues);

        return ContentUris.parseId(locationUri);
    }

    public static Cursor listNewWords(Context context) {
        return context.getContentResolver().query(
                WordEntity.CONTENT_URI,
                new String[]{WordEntity.COLUMN_WORD},
                WordEntity.COLUMN_DEFINITION + " IS NULL", // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
    }

    public static int update(ContentValues values, Context context) {

        values.put(WordContract.WordEntity.COLUMN_LAST_SEEN, getDbDateString(new Date()));

        int count = context.getContentResolver().update(WordEntity.CONTENT_URI,
                values,
                WordEntity.COLUMN_WORD + " = ?",
                new String[]{values.getAsString(WordEntity.COLUMN_WORD)});

        return count;
    }
}
