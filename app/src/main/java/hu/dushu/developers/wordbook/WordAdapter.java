package hu.dushu.developers.wordbook;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by renfeng on 1/2/15.
 */
public class WordAdapter extends CursorAdapter {

    private static final String LOG_TAG = WordAdapter.class.getSimpleName();

//    private final SimpleDateFormat format;

    private boolean twoPaneLayout;

    public WordAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);

//        format = new SimpleDateFormat("EEE MMM dd");

        return;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.word_history_list_item, parent, false);
        view.setTag(new ViewHolder(view));

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();

        String word = cursor.getString(hu.dushu.developers.wordbook.WordSelectorFragment.WORD_COLUMN);
        int viewCount = cursor.getInt(hu.dushu.developers.wordbook.WordSelectorFragment.VIEW_COUNT_COLUMN);
        String lastSeen = cursor.getString(hu.dushu.developers.wordbook.WordSelectorFragment.LAST_SEEN_COLUMN);

        holder.wordTextView.setText(word);
        holder.viewCountView.setText(viewCount + "");

        /*
         * this is why i need this adapter
         */
//        holder.lastSeenView.setText(format.format(WordContract.getDateFromDb(lastSeen)));
        holder.lastSeenView.setText(Utility.getFriendlyDayString(context, lastSeen));

        return;
    }

    private static class ViewHolder {

        public final TextView wordTextView;
        public final TextView viewCountView;
        public final TextView lastSeenView;

        public ViewHolder(View view) {
            wordTextView = (TextView) view.findViewById(R.id.word_history_word);
            viewCountView = (TextView) view.findViewById(R.id.word_history_view_count);
            lastSeenView = (TextView) view.findViewById(R.id.word_history_last_seen);
        }
    }

    public boolean isTwoPaneLayout() {
        return twoPaneLayout;
    }

    public void setTwoPaneLayout(boolean twoPaneLayout) {
        this.twoPaneLayout = twoPaneLayout;
    }
}
