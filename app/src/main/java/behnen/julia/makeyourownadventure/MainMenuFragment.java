package behnen.julia.makeyourownadventure;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import behnen.julia.makeyourownadventure.asyncs.AbstractDownloadImageTask;
import behnen.julia.makeyourownadventure.asyncs.AbstractDownloadStoryElementTask;
import behnen.julia.makeyourownadventure.model.StoryElement;

/**
 * A fragment that provides the central navigation of the app in the form of a menu.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class MainMenuFragment extends Fragment {

    private static final String TAG = "MainMenuFragment";

    /**
     * The current story element.
     */
    private StoryElement mStoryElement;

    private TextView mStoryTitle;
    private TextView mElementTitle;
    private ImageView mImage;
    private Button mContinueStoryButton;

    /**
     * The context which implements the interface methods.
     */
    private OnMainMenuInteractionListener mCallback;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnMainMenuInteractionListener {

        /**
         * Callback triggered when the fragment resumes.
         * @return An array containing the author, storyId, and elementId of the
         * current story element in that order.
         */
        String[] onMainMenuResume();

        /**
         * Callback triggered when the user has pressed the "Continue Story" button
         * in MainMenuFragment.
         */
        void onMainMenuContinueStoryAction(StoryElement storyElement, String storyTitle);

        /**
         * Callback triggered when the user has pressed the "Bookmarked Stories" button
         * in MainMenuFragment.
         */
        void onMainMenuBookmarkedStoriesAction();

        /**
         * Callback triggered when the user has pressed the "Create or Edit Stories" button
         * in MainMenuFragment.
         */
        void onMainMenuCreateEditStoriesAction();

        /**
         * Callback triggered when the user has pressed the "About" button
         * in MainMenuFragment.
         */
        void onMainMenuAboutAction();

        /**
         * Callback triggered when the user has pressed the "Sign Out" button
         * in MainMenuFragment.
         */
        void onMainMenuSignOutAction();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set everything related to the current story invisible/disabled until
        // there is a known current story.
        mStoryTitle.setVisibility(View.GONE);
        mElementTitle.setVisibility(View.GONE);
        mContinueStoryButton.setEnabled(false);
        getActivity().setTitle(R.string.main_menu_title);
        // Set logo as image until story element image downloaded
        mImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.logo));
        if (mCallback != null) {
            // Get current story element
            String[] elementInfo = mCallback.onMainMenuResume();
            // If story element exists, download it.
            if (elementInfo[1].length() != 0) {
                new StoryGetElementTask().execute(elementInfo[0], elementInfo[1], elementInfo[2]);
                Log.d(TAG, elementInfo[3]);
                mStoryTitle.setText(elementInfo[3]);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        mStoryTitle = (TextView) v.findViewById(R.id.main_menu_story_title);
        mElementTitle = (TextView) v.findViewById(R.id.main_menu_element_title);
        mImage = (ImageView) v.findViewById(R.id.main_menu_image);

        mContinueStoryButton = (Button) v.findViewById(R.id.main_menu_continue_story_button);
        Button downloadedStoriesButton =
                (Button) v.findViewById(R.id.main_menu_bookmarked_stories_button);
        Button myStoriesButton = (Button) v.findViewById(R.id.main_menu_create_edit_stories_button);
        Button aboutButton = (Button) v.findViewById(R.id.main_menu_about_button);
        Button signOutButton = (Button) v.findViewById(R.id.main_menu_sign_out_button);

        mContinueStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuContinueStoryAction(
                            mStoryElement, mStoryTitle.getText().toString());
                }
            }
        });

        downloadedStoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuBookmarkedStoriesAction();
                }
            }
        });

        myStoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuCreateEditStoriesAction();
                }
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuAboutAction();
                }
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuSignOutAction();
                }
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnMainMenuInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnMainMenuInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /**
     * Sets the fragment story element to be the downloaded current story element
     * and displays it.
     * @param element The current story element.
     */
    public void setStoryElement(StoryElement element) {
        mStoryElement = element;
        // download image
        new DownloadImageTask().execute(mStoryElement.getImageUrl());
        // enable story continue
        mContinueStoryButton.setEnabled(true);
        // set element info visible
        mElementTitle.setText(mStoryElement.getTitle());
        mStoryTitle.setVisibility(View.VISIBLE);
        mElementTitle.setVisibility(View.VISIBLE);
    }

    /**
     * Downloads a story element from the online database.
     */
    private class StoryGetElementTask extends AbstractDownloadStoryElementTask {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Parse JSON
            try {
                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("result");
                if (status.equalsIgnoreCase("success")) {
                    String elementString = jsonObject.getString("storyElement");
                    StoryElement element = StoryElement.parseJson(elementString);
                    setStoryElement(element);

                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getContext(), "Current story element download failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getContext(),
                        getActivity().getResources().getString(R.string.async_error),
                        Toast.LENGTH_SHORT).show();
            }
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
