package behnen.julia.makeyourownadventure.support;

import java.io.IOException;

/**
 * Created by Julia on 11/1/2015.
 */
public abstract class AbstractStoryCheckTask extends PostAsyncTask<String, Void, String> {

    private static final String TAG = "AbstractStoryCheckTask";
    private static final String CHECK_STORY_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/checkStory.php";

    @Override
    protected String doInBackground(String...params) {
        String author = params[0];
        String storyId = params[1];

        String urlParameters = "author=" + author
                + "&story_id=" + storyId;
        try {
            return downloadUrl(CHECK_STORY_URL, urlParameters, TAG);
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid";
        }
    }
}
