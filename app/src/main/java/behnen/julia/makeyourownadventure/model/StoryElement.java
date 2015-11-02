package behnen.julia.makeyourownadventure.model;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Julia on 10/27/2015.
 */
public final class StoryElement implements Serializable {

    public static final long serialVersionUID = 45212343214451L;

    private final int mId;
    private final String mTitle;
    private final String mImageUrl;
    private final String mDescription;
    private final boolean mIsEnding;
    private final int[] mChoiceIds;
    private final String[] mChoiceDescriptions;

    public StoryElement(int id) {
        this(id, "", null, "", false, new int[2], new String[2]);
    }

    // Constructs an ending
    public StoryElement(int id, String title, String imageUrl, String description) {
        this(id, title, imageUrl, description, true, new int[2], new String[2]);
    }

    public StoryElement(int id, String title, String imageUrl, String description,
                         boolean isEnding, int[] choiceIds,
                         String[] theChoiceDescriptions) {
        mId = id;
        mTitle = title;
        mImageUrl = imageUrl;
        mDescription = description;
        mIsEnding = isEnding;
        mChoiceIds = choiceIds.clone();
        mChoiceDescriptions = theChoiceDescriptions.clone();
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isEnding() {
        return mIsEnding;
    }

    public int[] getChoiceIds() {
        return mChoiceIds.clone();
    }

    public String[] getChoiceDescriptions() {
        return mChoiceDescriptions.clone();
    }

//    public JSONObject toJson() {
//        JSONObject element;
//        try {
//            element = new JSONObject();
//            element.put("id", mId);
//            element.put("title", mTitle);
//            element.put("imageUrl", mImageUrl);
//            element.put("description", mDescription);
//            element.put("choiceIds", new JSONArray(mChoiceIds));
//            element.put("choiceDescriptions", new JSONArray(mChoiceDescriptions));
//        } catch (JSONException e) {
//            element = null;
//        }
//        return element;
//    }
//
//    public StoryElement parseJson(String json) {
//
//    }
}
