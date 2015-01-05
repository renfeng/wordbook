package hu.dushu.developers.wordbook.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by renfeng on 12/31/14.
 */
public class WordAuthenticatorService extends Service {

    private WordAuthenticator authenticator;

    @Override
    public void onCreate() {
        setAuthenticator(new WordAuthenticator(this));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return getAuthenticator().getIBinder();
    }

    public WordAuthenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(WordAuthenticator authenticator) {
        this.authenticator = authenticator;
    }
}
