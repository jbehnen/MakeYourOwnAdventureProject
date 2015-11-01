package behnen.julia.makeyourownadventure.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Julia on 10/27/2015.
 */
public final class Story implements Serializable {

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
}
