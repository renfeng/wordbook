package hu.dushu.developers.wordbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import hu.dushu.developers.wordbook.sync.WordSyncAdapter;


public class MainActivity extends ActionBarActivity implements WordSelectorFragment.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

//    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("activity lifecycle", "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        if (findViewById(R.id.word_definition_container) != null) {
        boolean twoPane = getResources().getBoolean(R.bool.two_pane_layout);
        if (twoPane) {
            Log.d(LOG_TAG, "tablet");
        } else {
            Log.d(LOG_TAG, "phone");
        }

        if (savedInstanceState == null) {

            Intent intent = getIntent();
            String word = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (word != null) {
                onWordSelected(word);
            }

//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new WordSelectorFragment())
//                    .commit();
        }

        WordSyncAdapter.initializeSyncAdapter(this);

        return;
    }

    @Override
    protected void onStart() {
        Log.d("activity lifecycle", "onStart()");
        super.onStart();
        // The activity is about to become visible.
    }

    @Override
    protected void onResume() {
        Log.d("activity lifecycle", "onResume()");
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }

    @Override
    protected void onPause() {
        Log.d("activity lifecycle", "onPause()");
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }

    @Override
    protected void onStop() {
        Log.d("activity lifecycle", "onStop()");
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }

    @Override
    protected void onDestroy() {
        Log.d("activity lifecycle", "onDestroy()");
        super.onDestroy();
        // The activity is about to be destroyed.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWordSelected(String word) {
        // FIXME

        Log.d(LOG_TAG, "word: " + word);


        boolean twoPane = getResources().getBoolean(R.bool.two_pane_layout);
        if (twoPane) {
            WordDefinitionFragment definitionFragment = new WordDefinitionFragment();

            Bundle args = new Bundle();
            args.putString(WordDefinitionFragment.WORD_KEY, word);
            definitionFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.word_definition_container, definitionFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, WordDefinitionActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, word);
            startActivity(intent);
        }

        return;
    }
}
