package hu.dushu.developers.wordbook;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import hu.dushu.developers.wordbook.data.WordContract;

/**
 * Created by renfeng on 12/31/14.
 */
public class WordSelectorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = WordSelectorFragment.class.getSimpleName();

    private static final int WORD_HISTORY_LOADER = 0;

    // These indices are tied to WORD_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int ID_COLUMN = 0;
    public static final int WORD_COLUMN = 1;
    public static final int VIEW_COUNT_COLUMN = 2;
    public static final int LAST_SEEN_COLUMN = 3;
//    public static final int DEFINITION_COLUMN = 4;

    private static final String[] WORD_COLUMNS = {
            WordContract.WordEntity._ID,
            WordContract.WordEntity.COLUMN_WORD,
            WordContract.WordEntity.COLUMN_VIEW_COUNT,
            WordContract.WordEntity.COLUMN_LAST_SEEN};

    private static final String POSITION_KEY = "position";

    private WordAdapter adapter;
    private int position;
    private ListView listView;

    public WordSelectorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
//                getActivity(),
//                R.layout.word_history_list_item,
//                null,
//                new String[]{
//                        WordContract.WordEntity.COLUMN_WORD,
//                        WordContract.WordEntity.COLUMN_VIEW_COUNT,
//                        WordContract.WordEntity.COLUMN_LAST_SEEN},
//                new int[]{R.id.word_history_word,
//                        R.id.word_history_view_count,
//                        R.id.word_history_last_seen},
//                0);
        WordAdapter adapter = new WordAdapter(getActivity(), null, 0);
        setAdapter(adapter);

        ListView wordHistory = (ListView) rootView.findViewById(R.id.word_history);
        wordHistory.setAdapter(adapter);
        wordHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = getAdapter().getCursor();

                    /*
                     * must move cursor for two-panel layout
                     */
                if (cursor != null && cursor.moveToPosition(position)) {
                    String word = cursor.getString(WORD_COLUMN);
                    Callback callback = (Callback) getActivity();
                    callback.onWordSelected(word);
                }

                setPosition(position);

                return;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)) {
            int position = savedInstanceState.getInt(POSITION_KEY);
            setPosition(position);
        }

        setListView(wordHistory);

//        final AutoCompleteTextView wordInput =
//                (AutoCompleteTextView) rootView.findViewById(R.id.word_input);
//        wordInput.setAdapter(adapter);
        final TextView wordInput =
                (TextView) rootView.findViewById(R.id.word_input);
//        wordInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                Log.d(LOG_TAG, "text changed: " + s.toString());
//
//                /*
//                 * TODO update
//                 */
//
//                return;
//            }
//        });

        Button lookupButton = (Button) rootView.findViewById(R.id.lookup_button);
        lookupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(LOG_TAG, "lookup");

                String word = wordInput.getText().toString();
                Callback callback = (Callback) getActivity();
                callback.onWordSelected(word);

                return;
            }
        });


        return rootView;
    }

    public WordAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(WordAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        switch (i) {
            case WORD_HISTORY_LOADER: {

                String sortOrder = WordContract.WordEntity.COLUMN_LAST_SEEN + " DESC, " +
                        WordContract.WordEntity._ID + " DESC";

                return new CursorLoader(
                        getActivity(),
                        WordContract.WordEntity.CONTENT_URI,
                        WORD_COLUMNS,
                        null,
                        null,
                        sortOrder);
            }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        getListView().smoothScrollToPosition(getPosition());

        getAdapter().swapCursor(cursor);

        return;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        getAdapter().swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        public void onWordSelected(String item);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        getLoaderManager().initLoader(WORD_HISTORY_LOADER, null, this);

        boolean twoPaneLayout = !getResources().getBoolean(R.bool.two_pane_layout);
        getAdapter().setTwoPaneLayout(twoPaneLayout);

        super.onActivityCreated(savedInstanceState);

        return;
    }

    @Override
    public void onResume() {

        super.onResume();

        /*
         * TODO compare some app level settings?
         */
//        String newLocation = Utility.getPreferredLocation(getActivity());
//        if (mLocation != null && !mLocation.equals(newLocation)) {
//            getLoaderManager().restartLoader(WORD_HISTORY_LOADER, null, this);
////            mLocation = newLocation;
////            updateWeather();
//        }

        /*
         * forces reload to show the updated view count
         */
//        WordSyncAdapter.syncImmediately(getActivity());
        getLoaderManager().restartLoader(WORD_HISTORY_LOADER, null, this);

        return;
    }
}
