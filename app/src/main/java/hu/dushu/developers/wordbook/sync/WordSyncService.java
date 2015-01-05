package hu.dushu.developers.wordbook.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by renfeng on 12/31/14.
 */
public class WordSyncService extends Service {

    private static final Object lock = new Object();
    private static WordSyncAdapter adapter;

    @Override
    public void onCreate() {

        synchronized (lock) {
            if (getAdapter() == null) {
                setAdapter(new WordSyncAdapter(getApplicationContext(), true));
            }
        }

        return;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return getAdapter().getSyncAdapterBinder();
    }

    public static WordSyncAdapter getAdapter() {
        return adapter;
    }

    public static void setAdapter(WordSyncAdapter adapter) {
        WordSyncService.adapter = adapter;
    }
}
