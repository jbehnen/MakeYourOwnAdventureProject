package behnen.julia.makeyourownadventure;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import behnen.julia.makeyourownadventure.asyncs.AbstractDownloadImageTask;
import behnen.julia.makeyourownadventure.model.StoryElement;


/**
 * A fragment that displays a story element.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class StoryElementFragment extends Fragment {

    /**
     * The tag used to identify the story element argument in the bundle.
     */
    private static final String STORY_ELEMENT = "storyElement";
    /**
     * The tag used to identify the story title argument in the bundle.
     */
    private static final String STORY_TITLE = "storyTitle";
    /**
     * The tag used to identify the isOnline argument in the bundle.
     */
    private static final String IS_ONLINE = "isOnline";
    /**
     * The tag used to identify the isActive argument in the bundle.
     */
    private static final String IS_ACTIVE = "isActive";

    /**
     * The title of the overall story.
     */
    private String mStoryTitle;
    /**
     * True if the story element is stored online, false if it is
     * stored locally.
     */
    private boolean mIsOnline;
    /**
     * True if the story element is stored playable, false if it is
     * an inactive preview.
     */
    private boolean mIsActive;

    /**
     * The image view used to display the story element image.
     */
    private ImageView mImage;

    /**
     * The context which implements the interface methods.
     */
    private OnStoryElementInteractionListener mCallback;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnStoryElementInteractionListener {
        /**
         * Callback triggered when the user makes a choice (within the story).
         * @param storyTitle The title of the overall story.
         * @param author The author of the story.
         * @param storyId The storyId of the story.
         * @param elementId The elementId of the story element.
         * @param isOnline True if the element is stored online, false if it is stored locally.
         */
        void onStoryElementChoiceAction(
                String storyTitle, String author, String storyId, int elementId, boolean isOnline);
        /**
         * Callback triggered when the user chocses to restart the story.
         * @param storyTitle The title of the overall story.
         * @param author The author of the story.
         * @param storyId The storyId of the story.
         * @param isOnline True if the element is stored online, false if it is stored locally.
         */
        void onStoryElementRestartAction(
                String storyTitle, String author, String storyId, boolean isOnline);

        /**
         * Callback triggered when the user chooses to return to the main menu.
         */
        void onStoryElementMainMenuAction();
    }

    /**
     * DO NOT USE. Only create the fragment using the given newInstance method.
     */
    public StoryElementFragment() {

    }

    /**
     * Creates a new instance of StoryElementFragment.
     * @param storyTitle The title of the overall story.
     * @param storyElement The story element being displayed.
     * @param isOnline True if the element is stored online, false if it is stored locally.
     * @param isActive True if the element is playable, false if it is an inactive preview.
     * @return A new instance of StoryElementFragment.
     */
    public static StoryElementFragment newInstance(StoryElement storyElement, String storyTitle,
                                                   boolean isOnline, boolean isActive) {
        if (storyElement == null || storyTitle == null) {
            throw new IllegalArgumentException();
        }
        Bundle args = new Bundle();
        args.putString(STORY_ELEMENT, storyElement.toString());
        args.putString(STORY_TITLE, storyTitle);
        args.putBoolean(IS_ONLINE, isOnline);
        args.putBoolean(IS_ACTIVE, isActive);

        StoryElementFragment fragment = new StoryElementFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStoryElementInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnStoryElementInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    /**
     * Resumes fragment and updates activity action bar title.
     *
     * Action bar title update code adapted from
     * http://stackoverflow.com/questions/13472258/
     * handling-actionbar-title-with-the-fragment-back-stack
     */
    @Override
    public void onResume() {
        super.onResume();
        // Set title
        if (mIsActive) {
            getActivity().setTitle(R.string.story_element_title);
        } else {
            getActivity().setTitle(R.string.preview_title);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_story_element, container, false);

        mImage = (ImageView) v.findViewById(R.id.story_element_image);

        StoryElement storyElement = StoryElement.parseJson(getArguments().getString(STORY_ELEMENT));
        mStoryTitle = getArguments().getString(STORY_TITLE);
        mIsOnline = getArguments().getBoolean(IS_ONLINE);
        mIsActive = getArguments().getBoolean(IS_ACTIVE);

        displayStoryElement(v, storyElement);

        return v;
    }

    /**
     * Displays the story element.
     * @param v The view being updated.
     * @param storyElement The story element being displayed.
     */
    private void displayStoryElement(View v, StoryElement storyElement) {
        // Start image download
        new DownloadImageTask().execute(storyElement.getImageUrl());

        // Display text
        ((TextView) v.findViewById(R.id.story_element_story_title)).setText(mStoryTitle);
        ((TextView) v.findViewById(R.id.story_element_element_title))
                .setText(storyElement.getTitle());
        ((TextView) v.findViewById(R.id.story_element_description))
                .setText(storyElement.getDescription());

        // Display ending buttons if ending; choice buttons if choice.
        if (storyElement.isEnding()) {
            displayEndingButtons(v, storyElement);
        } else {
            displayChoiceButtons(v, storyElement);
        }
    }

    /**
     * Display the buttons used for an ending.
     * @param v The view being updated.
     * @param storyElement The story element being displayed.
     */
    private void displayEndingButtons(View v, final StoryElement storyElement) {
        Button backtrackButton = (Button) v.findViewById(R.id.story_element_button1);
        backtrackButton.setText(R.string.story_element_restart_button);

        Button mainMenuButton = (Button) v.findViewById(R.id.story_element_button2);
        mainMenuButton.setText(R.string.story_element_main_menu_button);

        if (mIsActive) {
            backtrackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onStoryElementRestartAction(mStoryTitle, storyElement.getAuthor(),
                                storyElement.getStoryId(), mIsOnline);
                    }
                }
            });
            mainMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onStoryElementMainMenuAction();
                    }
                }
            });
        }
    }

    /**
     * Display the buttons used for a choice.
     * @param v The view being updated.
     * @param storyElement The story element being displayed.
     */
    private void displayChoiceButtons(View v, final StoryElement storyElement) {
        Button choice1 = (Button) v.findViewById(R.id.story_element_button1);
        choice1.setText(storyElement.getChoice1Text());

        Button choice2 = (Button) v.findViewById(R.id.story_element_button2);
        choice2.setText(storyElement.getChoice2Text());

        if (mIsActive) {
            choice1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onStoryElementChoiceAction(mStoryTitle, storyElement.getAuthor(),
                                storyElement.getStoryId(), storyElement.getChoice1Id(), mIsOnline);
                    }
                }
            });
            choice2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onStoryElementChoiceAction(mStoryTitle, storyElement.getAuthor(),
                                storyElement.getStoryId(), storyElement.getChoice2Id(), mIsOnline);
                    }
                }
            });
        }
    }

    /**
     * An asynchronous task for downloading a story element image.
     */
    private class DownloadImageTask extends AbstractDownloadImageTask {
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImage.setImageBitmap(bitmap);
        }
    }
}
