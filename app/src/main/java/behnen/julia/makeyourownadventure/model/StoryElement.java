package behnen.julia.makeyourownadventure.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Julia on 10/27/2015.
 */
public final class StoryElement implements Serializable {

    private final int mId;
    private final String mTitle;
    private final Bitmap mImage;  // Are bitmaps immutable?
    private final String mDescription;
    private final boolean mIsEnding;
    private final int[] mChoiceIds;
    private final String[] mChoiceDescriptions;

    public StoryElement(int theId) {
        this(theId, "", null, "", false, new int[2], new String[2]);
    }

    public StoryElement(int id, String title, Bitmap image, String description,
                         boolean isEnding, int[] choiceIds,
                         String[] theChoiceDescriptions) {
        mId = id;
        mTitle = title;
        mImage = image;
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

    public Bitmap getImage() {
        return mImage;
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
}
