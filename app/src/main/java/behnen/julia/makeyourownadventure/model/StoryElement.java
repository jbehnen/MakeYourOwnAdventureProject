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
            element.put("storyId", mStoryId);
            element.put("elementId", mElementId);
            element.put("title", mTitle);
            element.put("imageUrl", mImageUrl);
            element.put("description", mDescription);
            element.put("isEnding", mIsEnding);
            element.put("choice1Id", mChoice1Id);
            element.put("choice2Id", mChoice2Id);
            element.put("choice1Text", mChoice1Text);
            element.put("choice2Text", mChoice2Text);
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
            String storyId = obj.getString("storyId");
            int elementId = obj.getInt("elementId");
            String title = obj.getString("title");
            String imageUrl = obj.getString("imageUrl");
            String description = obj.getString("description");
            boolean isEnding = obj.getBoolean("isEnding");
            int choice1Id = obj.getInt("choice1Id");
            int choice2Id = obj.getInt("choice2Id");
            String choice1Text = obj.getString("choice1Text");
            String choice2Text = obj.getString("choice2Text");
            storyElement = new StoryElement(author, storyId, elementId, title, imageUrl,
                    description, isEnding, choice1Id, choice2Id, choice1Text, choice2Text);
        } catch (JSONException e) {
            storyElement = null;
            e.printStackTrace();
        }
        return storyElement;
    }
}
