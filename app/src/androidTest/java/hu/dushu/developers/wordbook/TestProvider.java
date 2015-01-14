package hu.dushu.developers.wordbook;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import hu.dushu.developers.wordbook.data.WordContract;

/**
 * Created by renfeng on 12/31/14.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    private static final String WORD = "android";

    private static ContentValues createWordValues() {

        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(WordContract.WordEntity.COLUMN_WORD, WORD);
        testValues.put(WordContract.WordEntity.COLUMN_VIEW_COUNT, "100");
        testValues.put(WordContract.WordEntity.COLUMN_LAST_SEEN, "20141231");
        testValues.put(WordContract.WordEntity.COLUMN_DEFINITION, "android <b>definition</b>");
//        testValues.put(WordContract.WordEntity.COLUMN_DEFINITION_PLAIN, "android definition");

        return testValues;
    }

    // brings our database to an empty state
    public void deleteAllRecords() {

        getContext().getContentResolver().delete(
                WordContract.WordEntity.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = getContext().getContentResolver().query(
                WordContract.WordEntity.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        return;
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    public void testInsertReadProvider() {

        ContentValues testValues = TestDb.createWordValues();

        Uri locationUri = getContext().getContentResolver().insert(
                WordContract.WordEntity.CONTENT_URI, testValues);
        long wordRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(wordRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        // Now see if we can successfully query if we include the row id
        Cursor cursor = getContext().getContentResolver().query(
                WordContract.WordEntity.buildWordUri(wordRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, testValues);

        return;
    }

    public void testUpdateWord() {

        // Create a new map of values, where column names are the keys
        ContentValues values = TestDb.createWordValues();

        Uri locationUri = getContext().getContentResolver().
                insert(WordContract.WordEntity.CONTENT_URI, values);
        long rowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(rowId != -1);
        Log.d(LOG_TAG, "New row id: " + rowId);

        ContentValues updatedValues = new ContentValues(values);
//        updatedValues.put(WordContract.WordEntity._ID, rowId);
        updatedValues.put(WordContract.WordEntity.COLUMN_DEFINITION, "Santa's Village");

        int count = getContext().getContentResolver().update(
                WordContract.WordEntity.CONTENT_URI,
                updatedValues,
                WordContract.WordEntity.COLUMN_WORD + "= ?",
                new String[]{WORD});

        assertEquals(count, 1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = getContext().getContentResolver().query(
                WordContract.WordEntity.buildWordUri(rowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        TestDb.validateCursor(cursor, updatedValues);

        return;
    }

    public void test() {

        final String word = "rubric";

        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(WordContract.WordEntity.COLUMN_WORD, word);
        testValues.put(WordContract.WordEntity.COLUMN_DEFINITION, (String) null);
//        testValues.put(WordContract.WordEntity.COLUMN_DEFINITION_PLAIN, (String) null);
        testValues.put(WordContract.WordEntity.COLUMN_VIEW_COUNT, 1);
//        testValues.put(WordContract.WordEntity.COLUMN_LAST_SEEN,
//                WordContract.getDbDateString(new Date()));

        long wordRowId = WordContract.add(word, getContext());

        // Verify we got a row back.
        assertTrue(wordRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        {
            // A cursor is your primary interface to the query results.
            // Now see if we can successfully query if we include the row id
            Cursor cursor = getContext().getContentResolver().query(
                    WordContract.WordEntity.buildWordUri(wordRowId),
                    new String[]{
                            WordContract.WordEntity.COLUMN_WORD,
                            WordContract.WordEntity.COLUMN_VIEW_COUNT,
                            WordContract.WordEntity.COLUMN_DEFINITION,
//                            WordContract.WordEntity.COLUMN_DEFINITION_PLAIN,
                    },
                    WordContract.WordEntity.COLUMN_WORD + " = ? AND " +
                            WordContract.WordEntity.COLUMN_DEFINITION + " IS NULL", // cols for "where" clause
                    new String[]{word}, // values for "where" clause
                    null  // sort order
            );

            TestDb.validateCursor(cursor, testValues);
        }

        {
            ContentValues values = new ContentValues();
            values.put(WordContract.WordEntity.COLUMN_WORD, word);

            Cursor cursor = WordContract.listNewWords(getContext());

            TestDb.validateCursor(cursor, values);
        }

        {
            /*
             * update
             */
            ContentValues values = new ContentValues();
            values.put(WordContract.WordEntity.COLUMN_WORD, word);
            values.put(WordContract.WordEntity.COLUMN_DEFINITION, "rubric word definition");

            int count = WordContract.update(values, getContext());
            assertEquals("a single update expected", 1, count);
        }

        {
            Cursor cursor = WordContract.listNewWords(getContext());
            assertFalse(cursor.moveToFirst());
        }

        return;
    }
}
