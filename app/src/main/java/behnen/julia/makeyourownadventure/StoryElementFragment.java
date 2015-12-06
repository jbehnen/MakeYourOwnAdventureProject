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
 * A simple {@link Fragment} subclass.
 */
public class StoryElementFragment extends Fragment {

    private static final String TAG = "StoryElementFragment";

    private static final String STORY_ELEMENT = "storyElement";
    private static final String IS_ONLINE = "isOnline";
    private static final String IS_ACTIVE = "isActive";

    private ImageView mImage;
    private boolean mIsOnline;
    private boolean mIsActive;

    private OnStoryElementInteractionListener mCallback;

    public interface OnStoryElementInteractionListener {
        void onStoryElementChoiceAction(
                String author, String storyId, int elementId, boolean isOnline);
        void onStoryElementRestartAction(String author, String storyId, boolean isOnline);
        void onStoryElementMainMenuAction();
    }

    public static StoryElementFragment newInstance(StoryElement storyElement, boolean isOnline,
                                                   boolean isActive) {
        Bundle args = new Bundle();
        args.putString(STORY_ELEMENT, storyElement.toString());
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
                    + "must implement OnSignInInteractionListener");
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

        // TODO: test for bundle arguments existing/not null
        StoryElement storyElement = StoryElement.parseJson(getArguments().getString(STORY_ELEMENT));
        mIsOnline = getArguments().getBoolean(IS_ONLINE);
        mIsActive = getArguments().getBoolean(IS_ACTIVE);

        displayStoryElement(v, storyElement);

        return v;
    }

    private void displayStoryElement(View v, StoryElement storyElement) {
        new DownloadImageTask().execute(storyElement.getImageUrl());

        // TODO get title from shared prefs
        String title = "Temporary Title";

        ((TextView) v.findViewById(R.id.story_element_story_title)).setText(title);
        ((TextView) v.findViewById(R.id.story_element_element_title))
                .setText(storyElement.getTitle());
        ((TextView) v.findViewById(R.id.story_element_description))
                .setText(storyElement.getDescription());

        if (storyElement.isEnding()) {
            displayEndingButtons(v, storyElement);
        } else {
            displayChoiceButtons(v, storyElement);
        }
    }

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
                        mCallback.onStoryElementRestartAction(
                                storyElement.getAuthor(), storyElement.getStoryId(), mIsOnline);
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
                        mCallback.onStoryElementChoiceAction(storyElement.getAuthor(),
                                storyElement.getStoryId(), storyElement.getChoice1Id(), mIsOnline);
                    }
                }
            });
            choice2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onStoryElementChoiceAction(storyElement.getAuthor(),
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
