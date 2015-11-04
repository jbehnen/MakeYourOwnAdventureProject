package behnen.julia.makeyourownadventure.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Describes an immutable story header.
 *
 * A story element holds the metadata of a choose-your-own-adventure story. It holds the
 * title/story ID combination that identifies the story uniquely in a database and also
 * includes the title and description of the story.
 *
 * @author Julia Behnen
 * @version November 4, 2015
 */
public final class StoryHeader {

    /**
     * The author of the story that this StoryHeader represents.
     */
    private final String mAuthor;
    /**
     * The story ID of the story that this StoryHeader represents.
     */
    private final String mStoryId;
    /**
     * The title of the story that this StoryHeader represents.
     */
    private final String mTitle;
    /**
     * The title of the story that this StoryHeader represents.
     */
    private final String mDescription;

    /**
     * StoryHeader constructor.
     *
     * @param theAuthor The author of the story that this StoryHeader represents.
     * @param theId The story ID of the story that this StoryHeader represents.
     * @param theTitle  The title of the story that this StoryHeader represents.
     * @param theDescription The title of the story that this StoryHeader represents.
     */
    public StoryHeader(String theAuthor, String theId, String theTitle, String theDescription) {
        mTitle = theTitle;
        mStoryId = theId;
        mAuthor = theAuthor;
        mDescription = theDescription;
    }

    /**
     * Returns the StoryHeader's author.
     * @return The StoryHeader's author.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the StoryHeader's story ID.
     * @return The StoryHeader's story ID.
     */
    public String getStoryId() {
        return mStoryId;
    }

    /**
     * Returns the StoryHeader's title.
     * @return The StoryHeader's title.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the StoryHeader's description.
     * @return The StoryHeader's description.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Returns the StoryHeader object and its member fields as a JSON string.
     * @return The StoryHeader object and its member fields as a JSON string; returns
     * an empty JSONObject string if a JSON exception is thrown.
     */
    @Override
    public String toString() {
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

    /**
     * Returns the StoryHeader that is encoded in a JSON string.
     * @param json A JSON string; can be parsed to form a StoryHeader if encoded using the
     *             StoryHeader toString() format.
     * @return The StoryHeader encoded in the string if the string can be parsed successfully,
     * null otherwise.
     */
    public static StoryHeader parseJson(String json) {
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
