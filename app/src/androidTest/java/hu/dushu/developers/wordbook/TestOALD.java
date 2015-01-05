package hu.dushu.developers.wordbook;

import android.test.AndroidTestCase;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import hu.dushu.developers.wordbook.sync.WordSyncAdapter;

/**
 * Created by renfeng on 1/1/15.
 */
public class TestOALD extends AndroidTestCase {

    public final String LOG_TAG = TestOALD.class.getSimpleName();

    public void testExtract() throws IOException {

        Log.d(LOG_TAG, System.getProperty("user.dir"));
        Log.d(LOG_TAG, new File(".").toString());
//        Document document = Jsoup.parse(FileUtils.readFileToString(new File("rubric-oald.html")));
//        Document document = Jsoup.parse(IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("rubric-oald.html")));
        String html = WordSyncAdapter.parse(WordSyncAdapter.fetchWordDefinition("rubric"))/*.replaceAll("\n", "")*/;
//        Assert.assertEquals("<div xmlns=\"http://www.w3.org/1999/xhtml\" class=\"entry\" id=\"rubric\"><div class=\"h-g\" id=\"rubric__1\"><div class=\"top-container\"><div class=\"top-g\" id=\"rubric__2\"><span class=\"block-g\" id=\"rubric__10\"><span class=\"z\"> </span></span><div class=\"webtop-g\"><h2 class=\"h\">rubric</h2><span class=\"z\"> </span><span class=\"pos\">noun</span><!-- End of DIV webtop-g--></div><div class=\"ei-g\" id=\"rubric__7\"><span class=\"z\"> </span><span class=\"i\" id=\"rubric__8\">ˈruːbrɪk</span> <div class=\"sound audio_play_button pron-uk icon-audio\" data-src-mp3=\"http://www.oxfordlearnersdictionaries.com/media/english/uk_pron/r/rub/rubri/rubric__gb_1.mp3\" data-src-ogg=\"http://www.oxfordlearnersdictionaries.com/media/english/uk_pron_ogg/r/rub/rubri/rubric__gb_1.ogg\" title=\" pronunciation English\" style=\"cursor: pointer\" valign=\"top\">&nbsp;<!-- End of DIV sound audio_play_button pron-uk icon-audio--></div><span class=\"z\">; </span><span class=\"y\" id=\"rubric__9\">ˈruːbrɪk</span> <div class=\"sound audio_play_button pron-us icon-audio\" data-src-mp3=\"http://www.oxfordlearnersdictionaries.com/media/english/us_pron/r/rub/rubri/rubric__us_1.mp3\" data-src-ogg=\"http://www.oxfordlearnersdictionaries.com/media/english/us_pron_ogg/r/rub/rubri/rubric__us_1.ogg\" title=\" pronunciation American\" style=\"cursor: pointer\" valign=\"top\">&nbsp;<!-- End of DIV sound audio_play_button pron-us icon-audio--></div><span class=\"z\"> </span><!-- End of DIV ei-g--></div><span class=\"z\"> (</span><span class=\"z_r\">formal</span><span class=\"z\">)</span><!-- End of DIV top-g--></div><div class=\"clear\">&nbsp;<!-- End of DIV clear--></div><!-- End of DIV top-container--></div><div class=\"def_block\"><span class=\"d\" id=\"rubric__12\">a title or set of instructions written in a book, an exam paper, etc.</span><!-- End of DIV def_block--></div><!-- End of DIV h-g--></div><!-- End of DIV entry--></div>", html);

        return;
    }
}
