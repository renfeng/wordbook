package hu.dushu.developers.wordbook.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import hu.dushu.developers.wordbook.MainActivity;
import hu.dushu.developers.wordbook.R;
import hu.dushu.developers.wordbook.Utility;
import hu.dushu.developers.wordbook.data.WordContract;

/**
 * Created by renfeng on 12/31/14.
 */
public class WordSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = WordSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the weather, in milliseconds.
    // 60 seconds (1 minute) * 180 = 3 hours
//    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_INTERVAL = 30;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL;

//    private static final String[] NOTIFY_WORD_PROJECTION = new String[]{
//            WordContract.WordEntity.COLUMN_WORD,
////            WordContract.WordEntity.COLUMN_VIEW_COUNT,
////            WordContract.WordEntity.COLUMN_LAST_SEEN,
////            WordContract.WordEntity.COLUMN_DEFINITION
//    };
//
//    // these indices must match the projection
//    private static final int INDEX_WORD = 0;
////    private static final int INDEX_MAX_TEMP = 1;
////    private static final int INDEX_MIN_TEMP = 2;
////    private static final int INDEX_SHORT_DESC = 3;

    private static final int mId = 0;

    public WordSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(
            Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {

        Log.d(LOG_TAG, "perform sync");

        /*
         * list all new words (those without definition, i.e. null)
         */
        Cursor cursor = WordContract.listNewWords(getContext());
        if (cursor.moveToFirst()) {
            do {

                String word = cursor.getString(0);
//                String definition = fetchWordDefinition(word);
                Log.d(LOG_TAG, "word: " + word);

                String definition = parse(fetchWordDefinition(word));
                Log.d(LOG_TAG, "definition: " + definition);

                ContentValues values = new ContentValues();
//                values.put(WordContract.WordEntity.COLUMN_WORD, word);
                values.put(WordContract.WordEntity.COLUMN_DEFINITION, definition);
//                values.put(WordContract.WordEntity.COLUMN_VIEW_COUNT, 0);
//                values.put(WordContract.WordEntity.COLUMN_LAST_SEEN,
//                        WordContract.getDbDateString(new Date()));

                int count = getContext().getContentResolver().update(
                        WordContract.WordEntity.CONTENT_URI,
                        values,
                        WordContract.WordEntity.COLUMN_WORD + " = ?",
                        new String[]{word});
                Log.d(LOG_TAG, "updated word: " + word + ", " + count);

                notifyWord(word);

            } while (cursor.moveToNext());
        }


        return;
    }

    public static String parse(String html) {

        Document document = Jsoup.parse(html);
        Elements entryContent = document.select("#entryContent");
        return entryContent.html();
    }

    private void notifyWord(String word) {

        // notification here.
        if (Utility.isNotificationEnabled(getContext())) {
            /*
             * http://developer.android.com/guide/topics/ui/notifiers/notifications.html
             */
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Word Book")
                            .setContentText(word + " is ready for looking up.");
// Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(getContext(), MainActivity.class);
            resultIntent.putExtra(Intent.EXTRA_TEXT, word);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
// Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
            mNotificationManager.notify(mId, mBuilder.build());
        }

        return;
    }

    public static String fetchWordDefinition(String word) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw HTML response as a string.
        String html = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
//            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

            Uri uri = Uri.parse("http://www.oxfordlearnersdictionaries.com/definition/english/" + word);
            Uri.Builder ub = uri.buildUpon();
//            ub.appendQueryParameter("q", locationSetting);
//            ub.appendQueryParameter("mode", "json");
//            ub.appendQueryParameter("units", units);
//            ub.appendQueryParameter("cnt", days + "");
            URL url = new URL(ub.build().toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                html = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                html = null;
            }
            html = buffer.toString();

            Log.d(LOG_TAG, html);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            html = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        Log.d(LOG_TAG, html);
        if (html != null) {

        }

        return html;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        WordSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            /*
             * http://forums.udacity.com/questions/100242288/error-unparceling-bundle#ud853
             */
            SyncRequest.Builder builder = new SyncRequest.Builder();
            Bundle extras = new Bundle();
            builder.setExtras(extras);

            // we can enable inexact timers in our periodic sync
            SyncRequest request = builder.
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
