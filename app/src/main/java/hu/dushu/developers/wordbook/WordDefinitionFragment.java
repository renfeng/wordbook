package hu.dushu.developers.wordbook;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.net.URLDecoder;

import hu.dushu.developers.wordbook.data.WordContract;

/**
 * Created by renfeng on 12/31/14.
 */
public class WordDefinitionFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = WordDefinitionFragment.class.getSimpleName();

    private static final int WORD_LOADER = 0;
    public static final String WORD_KEY = "word";

    private String word;
    private String wordDefinition;

    private WebView definitionWebView;
    private TextView definitionTextView;
    private TextView viewCountTextView;
    private TextView lastSeenTextView;

    private ShareActionProvider shareActionProvider;

    public WordDefinitionFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_word_definition, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        /*
         * word definition is available when coming from notification, and for new words just being synced
         */
        String definition = getWordDefinition();
        if (definition != null) {
            shareActionProvider.setShareIntent(createShareIntent(definition));
        }

        setShareActionProvider(shareActionProvider);

        return;
    }

    private Intent createShareIntent(String definition) {

        String text = Jsoup.parse(definition).text();
        try {
            text = URLDecoder.decode(text, "UTF-8");
        } catch (Exception ex) {
            /*
             * ignore
             */
            Log.d(LOG_TAG, "failed to decode: " + ex.toString() + "\n" + definition);
        }

        Intent intent = new Intent(Intent.ACTION_SEND);

        /*
         * TODO default?
         */
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        /*
         * html is not available on Google+
         */
//        intent.setType("text/html");
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_TEXT, text + " #Wordbook");

        return intent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            setWord(savedInstanceState.getString(WORD_KEY));
        }

        View rootView = inflater.inflate(R.layout.fragment_word_definition, container, false);

        setDefinitionWebView((WebView) rootView.findViewById(R.id.definitionWebView));
        setDefinitionTextView((TextView) rootView.findViewById(R.id.definitionTextView));
        setViewCountTextView((TextView) rootView.findViewById(R.id.viewCountTextView));
        setLastSeenTextView((TextView) rootView.findViewById(R.id.lastSeenTextView));

        /*
         * TODO this doesn't help
         */
        getDefinitionWebView().getSettings().setDefaultTextEncodingName("utf-8");

        return rootView;
    }

    public String getWordDefinition() {
        return wordDefinition;
    }

    public void setWordDefinition(String wordDefinition) {
        this.wordDefinition = wordDefinition;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Log.v(LOG_TAG, "In onCreateLoader");

        switch (i) {
            case WORD_LOADER: {
                Uri uri = WordContract.WordEntity.buildWithWord(getWord());
                Log.v(LOG_TAG, uri.toString());

                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                return new CursorLoader(
                        getActivity(),
                        uri,
                        new String[]{
                                WordContract.WordEntity._ID,
                                WordContract.WordEntity.COLUMN_WORD,
                                WordContract.WordEntity.COLUMN_DEFINITION,
//                                WordContract.WordEntity.COLUMN_DEFINITION_PLAIN,
                                WordContract.WordEntity.COLUMN_VIEW_COUNT,
                                WordContract.WordEntity.COLUMN_LAST_SEEN},
                        null,
                        null,
                        null
                );
            }
        }

        return null;
    }

    @Override
    public void onResume() {
        super.onResume();


        /*
         * back from definition, settings, or other activity with back button
         */
//        Bundle arguments = getArguments();
//        if (arguments != null && arguments.containsKey(WordDefinitionActivity.DATE_KEY) &&
//                mLocation != null &&
//                !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
//            getLoaderManager().restartLoader(WORD_LOADER, null, this);
//        }
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(WORD_KEY)) {
            getLoaderManager().restartLoader(WORD_LOADER, null, this);
        }

        return;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if (cursor != null && cursor.moveToFirst()) {

            String word = cursor.getString(
                    cursor.getColumnIndex(WordContract.WordEntity.COLUMN_WORD));
            String definition = cursor.getString(
                    cursor.getColumnIndex(WordContract.WordEntity.COLUMN_DEFINITION));
            int viewCount = cursor.getInt(
                    cursor.getColumnIndex(WordContract.WordEntity.COLUMN_VIEW_COUNT));
            String lastSeen = cursor.getString(
                    cursor.getColumnIndex(WordContract.WordEntity.COLUMN_LAST_SEEN));

            if (definition != null) {
            /*
             * http://stackoverflow.com/questions/4543349/load-local-html-in-webview
             */
//            String prefix = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /></head><body>";
//            String affix = "</body></html>";
//            getDefinitionWebView().loadData(prefix + definition + affix, "text/html", "utf-8");

                /*
                 * http://stackoverflow.com/a/15604954/333033
                 */
                getDefinitionWebView().loadData(definition, "text/html; charset=utf-8", "utf-8");

//            getDefinitionWebView().loadData(definition, "text/html", "iso-8855-1");
                getDefinitionTextView().setText(definition);

                getViewCountTextView().setText(viewCount + "");

            /*
             * TODO format
             */
                getLastSeenTextView().setText(Utility.getFriendlyDayString(getActivity(), lastSeen));

                setWordDefinition(definition);

                if (getShareActionProvider() != null) {
                    getShareActionProvider().setShareIntent(createShareIntent(definition));
                }

            /*
             * update viewcount and lastseen
             */
                ContentValues values = new ContentValues();
                values.put(WordContract.WordEntity.COLUMN_WORD, word);
                values.put(WordContract.WordEntity.COLUMN_VIEW_COUNT, viewCount + 1);
                int count = WordContract.update(values, getActivity());
                if (count == 1) {
                    Log.d(LOG_TAG, "updated word: " + word);
                } else {
                    String msg = "failed to update word: " + word;
                    Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                String msg = "Come back later after the definition will be downloaded.";

//            Toast toast = new Toast(getActivity());
//            toast.setText(msg);
//            toast.show();
                Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
                toast.show();

            /*
             * nothing to share at the moment
             */
//            setWordDefinition(msg);

                getDefinitionWebView().loadData(msg, "text/plain", "utf-8");

            }
        } else {
            String msg = "You have found a new word. " +
                    "Come back later after the definition will be downloaded.";

//            Toast toast = new Toast(getActivity());
//            toast.setText(msg);
//            toast.show();
            Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
            toast.show();

            /*
             * nothing to share at the moment
             */
//            setWordDefinition(msg);

            getDefinitionWebView().loadData(msg, "text/plain", "utf-8");

            long id = WordContract.add(getWord(), getActivity());
            Log.d(LOG_TAG, "saved word: " + getWord() + "[" + id + "]");

            if (id == -1) {
                ContentValues values = new ContentValues();
                values.put(WordContract.WordEntity.COLUMN_WORD, word);

                /*
                 * TODO increment? requires query
                 */
                //values.put(WordContract.WordEntity.COLUMN_VIEW_COUNT, viewCount + 1);

                WordContract.update(values, getActivity());
            }
        }


        return;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(WORD_KEY)) {

            /*
             * it passes the word to next event handler of loader
             */
            setWord(arguments.getString(WORD_KEY));

            getLoaderManager().initLoader(WORD_LOADER, null, this);
        }

//        if (savedInstanceState != null) {
//            setWord(savedInstanceState.getString(WORD_KEY));
//        }

        super.onActivityCreated(savedInstanceState);

        return;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(WORD_KEY, getWord());
        super.onSaveInstanceState(outState);
    }

    public TextView getLastSeenTextView() {
        return lastSeenTextView;
    }

    public void setLastSeenTextView(TextView lastSeenTextView) {
        this.lastSeenTextView = lastSeenTextView;
    }

    public TextView getViewCountTextView() {
        return viewCountTextView;
    }

    public void setViewCountTextView(TextView viewCountTextView) {
        this.viewCountTextView = viewCountTextView;
    }

    public WebView getDefinitionWebView() {
        return definitionWebView;
    }

    public void setDefinitionWebView(WebView definitionWebView) {
        this.definitionWebView = definitionWebView;
    }

    public TextView getDefinitionTextView() {
        return definitionTextView;
    }

    public void setDefinitionTextView(TextView definitionTextView) {
        this.definitionTextView = definitionTextView;
    }

    public ShareActionProvider getShareActionProvider() {
        return shareActionProvider;
    }

    public void setShareActionProvider(ShareActionProvider provider) {
        this.shareActionProvider = provider;
    }
}
