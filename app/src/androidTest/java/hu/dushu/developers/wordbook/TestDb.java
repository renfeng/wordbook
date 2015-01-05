package hu.dushu.developers.wordbook;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import hu.dushu.developers.wordbook.data.WordContract;
import hu.dushu.developers.wordbook.data.WordDbHelper;

/**
 * Created by renfeng on 12/31/14.
 */
public class TestDb extends AndroidTestCase {

    private static final String LOG_TAG = TestDb.class.getSimpleName();

    static ContentValues createWordValues() {

        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(WordContract.WordEntity.COLUMN_WORD, "android");
        testValues.put(WordContract.WordEntity.COLUMN_VIEW_COUNT, "100");
        testValues.put(WordContract.WordEntity.COLUMN_LAST_SEEN, "20141231");
        testValues.put(WordContract.WordEntity.COLUMN_DEFINITION, "android definition");

        return testValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            if (entry.getValue() == null) {
                assertNull(valueCursor.getString(idx));
            } else {
                String expectedValue = entry.getValue().toString();
                assertEquals(expectedValue, valueCursor.getString(idx));
            }
        }
        valueCursor.close();
    }

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WordDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WordDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WordDbHelper dbHelper = new WordDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = createWordValues();

        long wordRowId = db.insert(WordContract.WordEntity.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue(wordRowId != -1);
        Log.d(LOG_TAG, "New row id: " + wordRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                WordContract.WordEntity.TABLE_NAME,  // Table to Query
                null, // leaving columns null just returns all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            validateCursor(cursor, values);
        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }

        dbHelper.close();

        return;
    }
}
