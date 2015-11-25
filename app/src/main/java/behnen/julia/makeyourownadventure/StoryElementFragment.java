package behnen.julia.makeyourownadventure;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import behnen.julia.makeyourownadventure.model.StoryElement;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoryElementFragment extends Fragment {

    private static final String TAG = "StoryElementFragment";

    private static final String STORY_ELEMENT = "storyElement";
    private static final String IS_ONLINE = "isOnline";

    /**
     * The URL for story element download requests from the shared image directory.
     */
    private static final String SHARED_IMAGES_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/images/shared/";

    private ImageView mImage;
    private boolean mIsOnline;

    private OnStoryElementInteractionListener mCallback;

    public interface OnStoryElementInteractionListener {
        void onStoryElementChoiceAction(
                String author, String storyId, int elementId, boolean isOnline);
        void onStoryElementBacktrackAction();
        void onStoryElementMainMenuAction();
    }

    public static StoryElementFragment newInstance(StoryElement storyElement, boolean isOnline) {
        Bundle args = new Bundle();
        args.putString(STORY_ELEMENT, storyElement.toString());
        args.putBoolean(IS_ONLINE, isOnline);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_story_element, container, false);

        mImage = (ImageView) v.findViewById(R.id.story_element_image);

        // TODO: test for bundle arguments existing/not null
        StoryElement storyElement = StoryElement.parseJson(getArguments().getString(STORY_ELEMENT));
        mIsOnline = getArguments().getBoolean(IS_ONLINE);

        displayStoryElement(v, storyElement);

        return v;
    }

    private void displayStoryElement(View v, StoryElement storyElement) {
        new DownloadImageTask().execute(SHARED_IMAGES_URL + storyElement.getImageUrl());

        // TODO get title from shared prefs
        String title = "Temporary Title";

        ((TextView) v.findViewById(R.id.story_element_story_title)).setText(title);
        ((TextView) v.findViewById(R.id.story_element_element_title))
                .setText(storyElement.getTitle());
        ((TextView) v.findViewById(R.id.story_element_description))
                .setText(storyElement.getDescription());

        if (storyElement.isEnding()) {
            displayEndingButtons(v);
        } else {
            displayChoiceButtons(v, storyElement);
        }
    }

    private void displayEndingButtons(View v) {
        Button backtrackButton = (Button) v.findViewById(R.id.story_element_button1);
        backtrackButton.setText(R.string.story_element_backtrack_button);
        backtrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onStoryElementBacktrackAction();
                }
            }
        });

        Button mainMenuButton = (Button) v.findViewById(R.id.story_element_button2);
        mainMenuButton.setText(R.string.story_element_main_menu_button);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onStoryElementMainMenuAction();
                }
            }
        });


        // TODO: wire up button
        ((Button) v.findViewById(R.id.story_element_button1))
                .setText(R.string.story_element_backtrack_button);
        // TODO: wire up button
        ((Button) v.findViewById(R.id.story_element_button2))
                .setText(R.string.story_element_main_menu_button);
    }

    private void displayChoiceButtons(View v, final StoryElement storyElement) {
        Button choice1 = (Button) v.findViewById(R.id.story_element_button1);
        choice1.setText(storyElement.getChoice1Text());
        choice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onStoryElementChoiceAction(storyElement.getAuthor(),
                            storyElement.getStoryId(), storyElement.getChoice1Id(), mIsOnline);
                }
            }
        });

        Button choice2 = (Button) v.findViewById(R.id.story_element_button2);
        choice2.setText(storyElement.getChoice2Text());
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

    /**
     * An asynchronous task for downloading a story element image.
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Starts the image download process.
         * @param urls The URL of the image.
         * @return A string holding the result of the request.
         */
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    InputStream is = new BufferedInputStream(urlObject.openStream());
                    bitmap = BitmapFactory.decodeStream(is);

                } catch (Exception e) {
//                Toast.makeText(getActivity(), "Unable to download the image, Reason: "
//                        + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImage.setImageBitmap(bitmap);
        }
    }
}
