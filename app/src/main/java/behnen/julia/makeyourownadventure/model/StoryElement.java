package behnen.julia.makeyourownadventure.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Julia on 10/27/2015.
 */
public final class StoryElement {

    private final String mAuthor;
    private final String mStoryId;
    private final int mElementId;
    private final String mTitle;
    private final String mImageUrl;
    private final String mDescription;
    private final boolean mIsEnding;
    private final int mChoice1Id;
    private final int mChoice2Id;
    private final String mChoice1Text;
    private final String mChoice2Text;

    public StoryElement(String author, String storyId, int elementId) {
        this(author, storyId, elementId, "", "", "", false, -1, -1, "", "");
    }

    // Constructs an ending
    public StoryElement(String author, String storyId, int elementId, String title,
                        String imageUrl, String description) {
        this(author, storyId, elementId, title, imageUrl, description, true, -1, -1, "", "");
    }

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
        mChoice1Id = choice1Id;
        mChoice2Id = choice2Id;
        mChoice1Text = choice1Text;
        mChoice2Text = choice2Text;
    }

    public String toJson() {
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

    private StoryElement parseJson(String json) {
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
}
