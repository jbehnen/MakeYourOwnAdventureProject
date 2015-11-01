package behnen.julia.makeyourownadventure.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Julia on 10/27/2015.
 */
public final class Story {

    public static final int START_ID = 0;
    private final String mAuthor;
    private final String mStoryId;

    private String mTitle;
    private String mDescription;
    private Map<Integer, StoryElement> mStoryElements;

    public Story(String theId, String theAuthor) {
        this(theId, theAuthor, "", "", new HashMap<Integer, StoryElement>());
        addStoryElement(new StoryElement(START_ID)); // 0 maps to the START story element
    }

    public Story(Story theOther) {
        this(theOther.mStoryId, theOther.mAuthor, theOther.mTitle, theOther.mDescription,
                theOther.mStoryElements);
    }

    public Story(String theId, String theAuthor, String theTitle, String theDescription,
                  Map<Integer, StoryElement> theStoryElements) {
        mTitle = theTitle;
        mStoryId = theId;
        mAuthor = theAuthor;
        mDescription = theDescription;
        mStoryElements = new HashMap<>(theStoryElements);
    }

    public Story(String theId, String theAuthor, String theTitle, String theDescription,
                 String theSerializedStoryElements) {
        mTitle = theTitle;
        mStoryId = theId;
        mAuthor = theAuthor;
        mDescription = theDescription;
        mStoryElements = deserializeStoryElements(theSerializedStoryElements);
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

    public String getSerializedStoryElements() {
        String serialized;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(mStoryElements);
            byte[] serializedBytes = os.toByteArray();
            serialized = new String(serializedBytes, StandardCharsets.UTF_8);
            oos.close();
            os.close();
        } catch (IOException ioe) {
           serialized = "";
        }
        return serialized;
    }

    private Map<Integer, StoryElement> deserializeStoryElements(String serialized) {
        Map<Integer, StoryElement> deserialized;
        byte[] serializedBytes = serialized.getBytes(StandardCharsets.UTF_8);
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(serializedBytes);
            ObjectInputStream ois = new ObjectInputStream(is);
            deserialized = (HashMap<Integer, StoryElement>) ois.readObject();
            ois.close();
            is.close();
        } catch (Exception e) {
            deserialized = new HashMap<>();
        }
        return deserialized;
    }
}
