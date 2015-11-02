package behnen.julia.makeyourownadventure.model;

import android.annotation.SuppressLint;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Julia on 10/27/2015.
 */
public final class Story implements Serializable {

    private static final String TAG = "Story";

    public static final long serialVersionUID = 423548034214907L;

    public static final int START_ID = 0;
    private final String mAuthor;
    private final String mStoryId;

    private String mTitle;
    private String mDescription;
    private Map<Integer, StoryElement> mStoryElements;


    @SuppressLint("UseSparseArrays") // Sparse Arrays aren't easily put into JSON; may fix later.
    public Story(String theAuthor, String theId) {
        this(theAuthor, theId, "", "", new HashMap<Integer, StoryElement>());
        addStoryElement(new StoryElement(START_ID)); // 0 maps to the START story element
    }

    public Story(Story theOther) {
        this(theOther.mAuthor, theOther.mStoryId, theOther.mTitle, theOther.mDescription,
                theOther.mStoryElements);
    }

    public Story(String theAuthor, String theId, String theTitle, String theDescription,
                  Map<Integer, StoryElement> theStoryElements) {
        mTitle = theTitle;
        mStoryId = theId;
        mAuthor = theAuthor;
        mDescription = theDescription;
        mStoryElements = new HashMap<>(theStoryElements);
    }

    public Story(String jsonStory) {
        this(deserializeStory(jsonStory));
        //this(parseJsonString(jsonStory));
    }

    public final void addStoryElement(StoryElement storyElement) {
        mStoryElements.put(storyElement.getId(), storyElement);
    }

    public final boolean removeStoryElement(int theStoryElementId) {
        if (theStoryElementId <= 1) {
            return false;
        }
        mStoryElements.remove(theStoryElementId);
        return true;
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

    public void setTitle(String theTitle) {
        mTitle = theTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String theDescription) {
        mDescription = theDescription;
    }

    public Map<Integer, StoryElement> getStoryElements() {
        return new HashMap<>(mStoryElements);
    }

    public void setStoryElements(Map<Integer, StoryElement> theStoryElements) {
        mStoryElements = new HashMap<>(theStoryElements);
    }

//    public String toJsonString() {
//        JSONObject story;
//        try {
//            story = new JSONObject();
//            story.put("author", mAuthor);
//            story.put("storyId", mStoryId);
//            story.put("title", mTitle);
//            story.put("description", mDescription);
//            story.put("elements", storyElementsToJsonString());
//        } catch (JSONException e) {
//            story = null;
//        }
//        return story.toString();
//    }
//
//    private JSONObject storyElementsToJsonString() {
//        Set<Integer> keys = mStoryElements.keySet();
//        for
//    }
//
//    private final Story parseJsonStory(String json) {
//
//    }
//
//    private final Map<Integer, StoryElement> parseJsonStoryElementsMap (String json) {
//        Map<Integer, StoryElement> map = new HashMap<Integer, StoryElement>();
//        try {
//            JSONObject jsonElements = new JSONObject(json);
//            Iterator<String> iter = jsonElements.keys();
//            while (iter.hasNext()) {
//                String key = iter.next();
//                String storyElement = jsonElements.getString(key);
//
//            }
//        } catch (JSONException e) {
//            map = null;
//        }
//        return map;
//    }

    // Base64 encoding adapted from
    // http://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string
    public String getSerializedStory() {
        String serialized;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(this);
            byte[] serializedBytes = os.toByteArray();
            serialized = Base64.encodeToString(serializedBytes, Base64.URL_SAFE);
            oos.close();
            os.close();
        } catch (IOException ioe) {
           serialized = "";
        }
        return serialized;
    }

    private static final Story deserializeStory(String serialized) {
        Story deserialized;
        try {
            byte[] serializedBytes = Base64.decode(serialized, Base64.URL_SAFE);
            ByteArrayInputStream is = new ByteArrayInputStream(serializedBytes);
            ObjectInputStream ois = new ObjectInputStream(is);
            deserialized = (Story) ois.readObject();
            ois.close();
            is.close();
        } catch (Exception e) {
            Log.d(TAG, "Deserialization exception: " + e.toString());
            deserialized = new Story("author", "id");
        }
        return deserialized;
    }
}
