package behnen.julia.makeyourownadventure.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Julia on 10/27/2015.
 */
public final class StoryHeader {

    private static final String TAG = "Story";

    public static final int START_ID = 0;
    private final String mAuthor;
    private final String mStoryId;
    private final String mTitle;
    private final String mDescription;

    public StoryHeader(String theAuthor, String theId) {
        this(theAuthor, theId, "", ""); // 0 maps to the START story element
    }

    public StoryHeader(StoryHeader theOther) {
        this(theOther.mAuthor, theOther.mStoryId, theOther.mTitle, theOther.mDescription);
    }

    public StoryHeader(String theAuthor, String theId, String theTitle, String theDescription) {
        mTitle = theTitle;
        mStoryId = theId;
        mAuthor = theAuthor;
        mDescription = theDescription;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getStoryId() {
        return mStoryId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String toJson() {
        JSONObject element;
        try {
            element = new JSONObject();
            element.put("author", mAuthor);
            element.put("story_id", mStoryId);
            element.put("title", mTitle);
            element.put("description", mDescription);
        } catch (JSONException e) {
            element = new JSONObject();
        }
        return element.toString();
    }

    public static final StoryHeader parseJson(String json) {
        StoryHeader storyHeader;
        try {
            JSONObject obj = new JSONObject(json);
            String author = obj.getString("author");
            String storyId = obj.getString("story_id");
            String title = obj.getString("title");
            String description = obj.getString("description");
            storyHeader = new StoryHeader(author, storyId, title, description);
        } catch (JSONException e) {
            storyHeader = null;
            e.printStackTrace();
        }
        return storyHeader;
    }
}
