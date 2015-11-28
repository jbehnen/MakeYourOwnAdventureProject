package behnen.julia.makeyourownadventure.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Describes an immutable story element.
 *
 * A story element is an atomic piece of a choose-your-own-adventure story. It can be either a
 * choice or an ending. A choice element includes two options, each of which points to another
 * story element. An ending does not. A story element is uniquely identified in a database by
 * its author, story ID, and element ID, thus only one story element with this combination can
 * be permanently stored at any time.
 *
 * @author Julia Behnen
 * @version November 4, 2015
 */
public final class StoryElement {

    /**
     * The element ID of the StoryElement that starts the story with the same author and story ID.
     */
    public static final int START_ID = 0;

    public static final String DEFAULT_TITLE = "[Default Title]";
    public static final String DEFAULT_DESCRIPTION = "[Default Description]";
    public static final String DEFAULT_IMAGE_URL = "trees_1.jpg";

    /**
     * The author of the story that this StoryElement is associated with.
     */
    private final String mAuthor;
    /**
     * The story ID of the story that this StoryElement is associated with.
     */
    private final String mStoryId;
    /**
     * The element ID of the StoryElement.
     */
    private final int mElementId;
    /**
     * The title of the StoryElement.
     */
    private final String mTitle;
    /**
     * The URL of the image used in the StoryElement.
     */
    private final String mImageUrl;
    /**
     * The description of the StoryElement.
     */
    private final String mDescription;
    /**
     * True if the StoryElement is an ending, false otherwise.
     */
    private final boolean mIsEnding;
    /**
     * The element ID of the StoryElement that the first choice points to.
     */
    private final int mChoice1Id;
    /**
     * The element ID of the StoryElement that the second choice points to.
     */
    private final int mChoice2Id;
    /**
     * The text description of the first choice.
     */
    private final String mChoice1Text;
    /**
     * The text description of the second choice.
     */
    private final String mChoice2Text;

    // TODO: reorganize constructors

    /**
     * StoryElement constructor.
     *
     * @param author The author of the story that this StoryElement is associated with.
     * @param storyId The story ID of the story that this StoryElement is associated with.
     * @param elementId The element ID of the StoryElement.
     * @param title The title of the StoryElement.
     * @param imageUrl The URL of the image used in the StoryElement.
     * @param description The description of the StoryElement.
     * @param isEnding True if the StoryElement is an ending, false otherwise.
     * @param choice1Id The element ID of the StoryElement that the first choice points to.
     * @param choice2Id The element ID of the StoryElement that the second choice points to.
     * @param choice1Text The text description of the first choice.
     * @param choice2Text The text description of the second choice.
     */
    public StoryElement(String author, String storyId, int elementId, String title, String imageUrl,
                        String description, boolean isEnding, int choice1Id, int choice2Id,
                         String choice1Text, String choice2Text) {
        mAuthor = author;
        mStoryId = storyId;
        mElementId = elementId;
        mTitle = title;
        mImageUrl = imageUrl;
        mDescription = description;
        mIsEnding = isEnding;
        if (isEnding) {
            mChoice1Id = elementId;
            mChoice2Id = elementId;
            mChoice1Text = "";
            mChoice2Text = "";
        } else {
            mChoice1Id = choice1Id;
            mChoice2Id = choice2Id;
            mChoice1Text = choice1Text;
            mChoice2Text = choice2Text;
        }
    }

    /**
     * StoryElement constructor. Sufficient to construct a fully-described choice.
     *
     * @param author The author of the story that this StoryElement is associated with.
     * @param storyId The story ID of the story that this StoryElement is associated with.
     * @param elementId The element ID of the StoryElement.
     * @param title The title of the StoryElement.
     * @param imageUrl The URL of the image used in the StoryElement.
     * @param description The description of the StoryElement.
     * @param choice1Id The element ID of the StoryElement that the first choice points to.
     * @param choice2Id The element ID of the StoryElement that the second choice points to.
     * @param choice1Text The text description of the first choice.
     * @param choice2Text The text description of the second choice.
     */
    public StoryElement(String author, String storyId, int elementId, String title, String imageUrl,
                        String description, int choice1Id, int choice2Id,
                        String choice1Text, String choice2Text) {
        mAuthor = author;
        mStoryId = storyId;
        mElementId = elementId;
        mTitle = title;
        mImageUrl = imageUrl;
        mDescription = description;
        mIsEnding = false;
        mChoice1Id = choice1Id;
        mChoice2Id = choice2Id;
        mChoice1Text = choice1Text;
        mChoice2Text = choice2Text;
    }

    /**
     * StoryElement constructor which is sufficient to construct a fully-described ending.
     *
     * @param author The author of the story that this StoryElement is associated with.
     * @param storyId The story ID of the story that this StoryElement is associated with.
     * @param elementId The element ID of the StoryElement.
     * @param title The title of the StoryElement.
     * @param imageUrl The URL of the image used in the StoryElement.
     * @param description The description of the StoryElement.
     */
    public StoryElement(String author, String storyId, int elementId, String title,
                        String imageUrl, String description) {
        this(author, storyId, elementId, title, imageUrl, description, true, elementId,
                elementId, "", "");
    }

    public StoryElement(String author, String storyId, int elementId) {
        this(author, storyId, elementId, DEFAULT_TITLE, DEFAULT_IMAGE_URL, DEFAULT_DESCRIPTION,
                elementId, elementId, "", "");
    }

    /**
     * Returns the StoryElement's author.
     * @return The StoryElement's author.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the StoryElement's story ID.
     * @return The StoryElement's story ID.
     */
    public String getStoryId() {
        return mStoryId;
    }

    /**
     * Returns the StoryElement's element ID.
     * @return The StoryElement's element ID.
     */
    public int getElementId() {
        return mElementId;
    }

    /**
     * Returns the StoryElement's title.
     * @return The StoryElement's title.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the StoryElement's image URL.
     * @return The StoryElement's image URL.
     */
    public String getImageUrl() {
        return mImageUrl;
    }

    /**
     * Returns the StoryElement's description.
     * @return The StoryElement's description.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Returns true if the StoryElement is an ending, false otherwise.
     * @return The StoryElement's author.
     */
    public boolean isEnding() {
        return mIsEnding;
    }

    /**
     * Returns the StoryElement's choice 1 element ID.
     * @return The StoryElement's choice 1 element ID.
     */
    public int getChoice1Id() {
        return mChoice1Id;
    }

    /**
     * Returns the StoryElement's choice 2 element ID.
     * @return The StoryElement's choice 2 element ID.
     */
    public int getChoice2Id() {
        return mChoice2Id;
    }

    /**
     * Returns the StoryElement's choice 1 text.
     * @return The StoryElement's choice 1 text.
     */
    public String getChoice1Text() {
        return mChoice1Text;
    }

    /**
     * Returns the StoryElement's choice 2 text.
     * @return The StoryElement's choice 2 text.
     */
    public String getChoice2Text() {
        return mChoice2Text;
    }

    /**
     * Returns the StoryElement object and its member fields as a JSON string.
     * @return The StoryElement object and its member fields as a JSON string; returns
     * an empty JSONObject string if a JSON exception is thrown.
     */
    @Override
    public String toString() {
        JSONObject element;
        try {
            element = new JSONObject();
            element.put("author", mAuthor);
            element.put("story_id", mStoryId);
            element.put("element_id", mElementId);
            element.put("title", mTitle);
            element.put("image_url", mImageUrl);
            element.put("description", mDescription);
            element.put("is_ending", mIsEnding);
            element.put("choice1_id", mChoice1Id);
            element.put("choice2_id", mChoice2Id);
            element.put("choice1_text", mChoice1Text);
            element.put("choice2_text", mChoice2Text);
        } catch (JSONException e) {
            element = new JSONObject();
        }
        return element.toString();
    }

    /**
     * Returns the StoryElement that is encoded in a JSON string.
     * @param json A JSON string; can be parsed to form a StoryElement if encoded using the
     *             StoryElement toString() format.
     * @return The StoryElement encoded in the string if the string can be parsed successfully,
     * null otherwise.
     */
    public static StoryElement parseJson(String json) {
        StoryElement storyElement;
        try {
            JSONObject obj = new JSONObject(json);
            String author = obj.getString("author");
            String storyId = obj.getString("story_id");
            int elementId = obj.getInt("element_id");
            String title = obj.getString("title");
            String imageUrl = obj.getString("image_url");
            String description = obj.getString("description");
            boolean isEnding = obj.getBoolean("is_ending");
            int choice1Id = obj.getInt("choice1_id");
            int choice2Id = obj.getInt("choice2_id");
            String choice1Text = obj.getString("choice1_text");
            String choice2Text = obj.getString("choice2_text");
            storyElement = new StoryElement(author, storyId, elementId, title, imageUrl,
                    description, isEnding, choice1Id, choice2Id, choice1Text, choice2Text);
        } catch (JSONException e) {
            storyElement = null;
            e.printStackTrace();
        }
        return storyElement;
    }

    public String toTargetDescriptionString() {
        return mElementId + ": " + mTitle;
    }
}
