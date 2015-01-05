package hu.dushu.developers.wordbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by renfeng on 12/31/14.
 */
public class WordDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "word.db";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    public WordDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + WordContract.WordEntity.TABLE_NAME + " (" +
                WordContract.WordEntity._ID + " INTEGER PRIMARY KEY, " +
                WordContract.WordEntity.COLUMN_WORD + " TEXT UNIQUE NOT NULL, " +
                WordContract.WordEntity.COLUMN_DEFINITION + " TEXT, " +
                WordContract.WordEntity.COLUMN_VIEW_COUNT + " INTEGER NOT NULL, " +
                WordContract.WordEntity.COLUMN_LAST_SEEN + " TEXT NOT NULL, " +
                "UNIQUE (" + WordContract.WordEntity.COLUMN_WORD + ") ON CONFLICT IGNORE);");

        return;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WordContract.WordEntity.TABLE_NAME);
        onCreate(db);
    }
}
