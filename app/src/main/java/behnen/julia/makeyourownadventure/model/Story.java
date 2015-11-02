package behnen.julia.makeyourownadventure.model;

/**
 * Created by Julia on 10/27/2015.
 */
public final class Story {

    private static final String TAG = "Story";

    public static final int START_ID = 0;
    private final String mAuthor;
    private final String mStoryId;
    private final String mTitle;
    private final String mDescription;

    public Story(String theAuthor, String theId) {
        this(theAuthor, theId, "", ""); // 0 maps to the START story element
    }

    public Story(Story theOther) {
        this(theOther.mAuthor, theOther.mStoryId, theOther.mTitle, theOther.mDescription);
    }

    public Story(String theAuthor, String theId, String theTitle, String theDescription) {
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

    public int newStoryElement() {
        // TODO: implement
        return -1;
    }
}
