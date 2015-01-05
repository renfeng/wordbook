package hu.dushu.developers.wordbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class WordDefinitionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_definition);
        if (savedInstanceState == null) {

            /*
             * TODO what does the args do here? restore after returning from settings?
             */

            Intent intent = getIntent();
            String word = intent.getStringExtra(Intent.EXTRA_TEXT);

            Bundle args = new Bundle();
            args.putString(WordDefinitionFragment.WORD_KEY, word);

            setTitle(word);

            /*
             * TODO how to get the up button in the action bar?
             */

            WordDefinitionFragment fragment = new WordDefinitionFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.word_definition_container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /*
         * menu moved to fragment
         */
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_word_definition, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
