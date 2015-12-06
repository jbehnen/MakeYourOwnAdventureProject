package behnen.julia.makeyourownadventure.asyncs;

import java.io.IOException;

/**
 * Downloads a StoryElement from the online database.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public abstract class AbstractDownloadStoryElementTask
        extends AbstractPostAsyncTask<String, Void, String> {

    /**
     * The URL for story element download requests.
     */
    private static final String GET_STORY_ELEMENT_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/getStoryElement.php";
    private static final String TAG = "AbstractDownloadStoryElementTask";

    /**
     * Starts the story element retrieval process.
     * @param params The story header author, story ID, and element ID, in that order.
     * @return A string holding the result of the request.
     */
    @Override
    protected String doInBackground(String...params) {
        String author = params[0];
        String storyId = params[1];
        String elementId = params[2];

        String urlParameters = "author=" + author
                + "&story_id=" + storyId
                + "&element_id=" + elementId;
        try {
            return downloadUrl(GET_STORY_ELEMENT_URL, urlParameters, TAG);
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid";
        }
    }
}

