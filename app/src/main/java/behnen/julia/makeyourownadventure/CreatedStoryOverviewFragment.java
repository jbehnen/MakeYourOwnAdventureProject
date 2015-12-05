package behnen.julia.makeyourownadventure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import behnen.julia.makeyourownadventure.asyncs.AbstractPostAsyncTask;
import behnen.julia.makeyourownadventure.dialogs.ContentSharingDialogFragment;
import behnen.julia.makeyourownadventure.dialogs.EditStoryHeaderDialogFragment;
import behnen.julia.makeyourownadventure.model.StoryElement;
import behnen.julia.makeyourownadventure.model.StoryHeader;

/**
 * Created by Julia on 11/22/2015.
 */
public class CreatedStoryOverviewFragment extends Fragment {

    private static final String TAG = "CreatedStoryOverview";

    private static final String ARG_STORY_HEADER = "storyHeader";

    /**
     * The URL for story registration requests.
     */
    private static final String DELETE_STORY_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/deleteStoryHeader.php";

    private static final int REQUEST_HEADER = 0;
    private static final int REQUEST_CONTENT_SHARING = 1;

    /**
     * The URL for story header upload requests.
     */
    private static final String UPLOAD_STORY_HEADER_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/uploadStoryHeader.php";
    /**
     * The URL for story element upload requests.
     */
    private static final String UPLOAD_STORY_ELEMENT_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/uploadStoryElement.php";

    private Button mEditStoryHeaderButton;
    private Button mEditStoryElementsButton;
    private Button mPlayButton;
    private Button mDeleteButton;
    private Button mUploadButton;

    private OnCreatedStoryOverviewInteractionListener mCallback;

    private StoryHeader mStoryHeader;

    public interface OnCreatedStoryOverviewInteractionListener {
        boolean onCreatedStoryOverviewUpdateHeader(StoryHeader storyHeader);
        void onCreatedStoryOverviewEditElements(String author, String storyId);
        void onCreatedStoryOverviewPlayStory(String author, String storyId);
        boolean onCreatedStoryOverviewDeleteLocalStory(String author, String storyId);
        void onCreatedStoryOverviewOnCompletedUpload(StoryHeader storyHeader);
        boolean onCreatedStoryOverviewDeleteStoryElement(
                String author, String storyId, int elementId);
        List<StoryElement> onCreatedStoryOverviewGetStoryElements(String author, String storyId);
        boolean onCreatedStoryOverviewStoryElementsExist(String author, String storyId);
        boolean onCreatedStoryIsStoryFinal(String author, String storyId);
        boolean onCreatedStorySetStoryFinal(String author, String storyId);
    }

    public static CreatedStoryOverviewFragment newInstance(StoryHeader storyHeader) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_STORY_HEADER, storyHeader.toString());

        CreatedStoryOverviewFragment fragment = new CreatedStoryOverviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_created_story_overview, container, false);

        // TODO: ensure that this argument doesn't crash the code if empty
        mStoryHeader =
                StoryHeader.parseJson((String) getArguments().getSerializable(ARG_STORY_HEADER));

        displayStoryInfo(view);

        mEditStoryHeaderButton =
                (Button) view.findViewById(R.id.created_story_overview_edit_story_info_button);
        mEditStoryHeaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editStoryHeader();
            }
        });

        mEditStoryElementsButton =
                (Button) view.findViewById(R.id.created_story_overview_edit_story_elements_button);
        mEditStoryElementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editStoryElements();
            }
        });

        mPlayButton = (Button) view.findViewById(R.id.created_story_overview_play_button);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playStory();
            }
        });

        mDeleteButton = (Button) view.findViewById(R.id.created_story_overview_delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStory();
            }
        });

        mUploadButton = (Button) view.findViewById(R.id.created_story_overview_upload_button);
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storyUpload();
            }
        });

        return view;
    }

    private void displayStoryInfo(View view) {
        if (mStoryHeader != null) {
            TextView author = (TextView) view.findViewById(R.id.created_story_overview_author);
            TextView storyId = (TextView) view.findViewById(R.id.created_story_overview_story_id);
            TextView title = (TextView) view.findViewById(R.id.created_story_overview_title);
            TextView description =
                    (TextView) view.findViewById(R.id.created_story_overview_description);

            author.setText(mStoryHeader.getAuthor());
            storyId.setText(mStoryHeader.getStoryId());
            title.setText(mStoryHeader.getTitle());
            description.setText(mStoryHeader.getDescription());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCreatedStoryOverviewInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    // TODO make sure this string is correct in all classes
                    + "must implement OnCreatedStoryOverviewInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_HEADER && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "request code: " + requestCode);
            String author = mStoryHeader.getAuthor();
            String storyId = mStoryHeader.getStoryId();
            String title = (String) data.getSerializableExtra(
                    EditStoryHeaderDialogFragment.EXTRA_TITLE);
            String description = (String) data.getSerializableExtra(
                    EditStoryHeaderDialogFragment.EXTRA_DESCRIPTION);
            if (mCallback != null) {
                Log.d(TAG, "mCallback" + author + storyId + title + description);
                mStoryHeader = new StoryHeader(author, storyId, title, description);
                mCallback.onCreatedStoryOverviewUpdateHeader(mStoryHeader);
                View view = getView();
                displayStoryInfo(view);
            }
        } else if (requestCode == REQUEST_CONTENT_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "I just uploaded a new story on " +
                        "Make Your Own Adventure. Check it out! Author: " +
                        mStoryHeader.getAuthor() + ", Story ID: " + mStoryHeader.getStoryId());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(
                        sendIntent, getResources().getText(R.string.send_to)));
            }
            getFragmentManager().popBackStackImmediate();
        }
    }

    private void editStoryHeader() {
        if (mCallback.onCreatedStoryIsStoryFinal(
                mStoryHeader.getAuthor(), mStoryHeader.getStoryId())) {
            Toast.makeText(getContext(),
                    "Story is partially uploaded and can no longer be edited.",
                    Toast.LENGTH_LONG).show();
        } else {
            EditStoryHeaderDialogFragment fragment =
                    EditStoryHeaderDialogFragment.newInstance(
                            mStoryHeader.getTitle(), mStoryHeader.getDescription());
            fragment.setTargetFragment(CreatedStoryOverviewFragment.this, REQUEST_HEADER);
            fragment.show(getFragmentManager(), "editStoryHeader");
        }
    }

    private void editStoryElements() {
        if (mCallback != null) {
            if (mCallback.onCreatedStoryIsStoryFinal(
                    mStoryHeader.getAuthor(), mStoryHeader.getStoryId())) {
                Toast.makeText(getContext(),
                        "Story is partially uploaded and can no longer be edited.",
                        Toast.LENGTH_LONG).show();
            } else {
                mCallback.onCreatedStoryOverviewEditElements(
                        mStoryHeader.getAuthor(), mStoryHeader.getStoryId());
            }
        }
    }

    private void playStory() {
        if (mCallback != null) {
            if (mCallback.onCreatedStoryIsStoryFinal(
                    mStoryHeader.getAuthor(), mStoryHeader.getStoryId())) {
                Toast.makeText(getContext(),
                        "Story is partially uploaded and can no longer be play-tested.",
                        Toast.LENGTH_LONG).show();
            } else {
                mCallback.onCreatedStoryOverviewPlayStory(mStoryHeader.getAuthor(),
                        mStoryHeader.getStoryId());
            }
        }
    }

    private void deleteStory() {
        if (mCallback != null) {
            String author = mStoryHeader.getAuthor();
            String storyId = mStoryHeader.getStoryId();
            if (mCallback.onCreatedStoryIsStoryFinal(author, storyId)) {
                Toast.makeText(getContext(),
                        "Story is partially uploaded and can no longer be deleted.",
                        Toast.LENGTH_LONG).show();
            } else {
                boolean deleted =
                        mCallback.onCreatedStoryOverviewDeleteLocalStory(author, storyId);
                if (deleted) {
                    // Delete story online
                    new StoryDeleteTask().execute(author, storyId);
                    getFragmentManager().popBackStackImmediate();
                } else {
                    Toast.makeText(
                            getActivity(), "Deletion failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Attempts to upload a story: both the story header and the story elements. The
     * story elements are uploaded first. If all of the story elements are uploaded
     * successfully, then the story header is uploaded.
     */
    private void storyUpload() {
        if (mCallback != null) {

            // Disable all buttons once story upload starting.
            mEditStoryHeaderButton.setEnabled(false);
            mEditStoryElementsButton.setEnabled(false);
            mPlayButton.setEnabled(false);
            mDeleteButton.setEnabled(false);
            mUploadButton.setEnabled(false);

            // Set story final so that can no longer be edited if initial upload fails.
            mCallback.onCreatedStorySetStoryFinal(
                    mStoryHeader.getAuthor(), mStoryHeader.getStoryId());

            List<StoryElement> storyElements = mCallback.onCreatedStoryOverviewGetStoryElements(
                    mStoryHeader.getAuthor(), mStoryHeader.getStoryId());

            for (StoryElement element: storyElements) {
                new StoryElementUploadTask().execute(element.getAuthor(),
                        element.getStoryId(), Integer.toString(element.getElementId()),
                        element.getTitle(), element.getImageUrl(), element.getDescription(),
                        Boolean.toString(element.isEnding()),
                        Integer.toString(element.getChoice1Id()),
                        Integer.toString(element.getChoice2Id()),
                        element.getChoice1Text(), element.getChoice2Text());
            }
        }
    }

    private void afterStoryElementUpload(String author, String storyId, int elementId) {
        if (mCallback != null) {
            mCallback.onCreatedStoryOverviewDeleteStoryElement(
                    author, storyId, elementId);
            if (!mCallback.onCreatedStoryOverviewStoryElementsExist(
                    author, storyId)) {
                new StoryHeaderUploadTask().execute(
                        mStoryHeader.getAuthor(), mStoryHeader.getStoryId(),
                        mStoryHeader.getTitle(), mStoryHeader.getDescription());
            }
        }
    }

    private void afterStoryHeaderUpload() {
        if (mCallback != null) {
            mCallback.onCreatedStoryOverviewOnCompletedUpload(mStoryHeader);
            ContentSharingDialogFragment fragment = new ContentSharingDialogFragment();
            fragment.setTargetFragment(CreatedStoryOverviewFragment.this, REQUEST_CONTENT_SHARING);
            fragment.show(getFragmentManager(), "editStoryHeader");
        }
    }

    /**
     * Uploads a StoryHeader to the online database. This alters the existing placeholder
     * StoryHeader uploaded for the story, which would previously contain null values for title
     * and description. This upload occurs only when the final version of the story is being
     * added to the database.
     */
    public class StoryHeaderUploadTask extends AbstractPostAsyncTask<String, Void, String> {

        /**
         * Starts the story header upload process.
         * @param params The story header author, story ID, title, and description, in that order.
         * @return A string holding the result of the request.
         */
        @Override
        protected String doInBackground(String...params) {

            String urlParameters = "author=" + params[0]
                    + "&story_id=" + params[1]
                    + "&title=" + params[2]
                    + "&description=" + params[3];
            try {
                return downloadUrl(UPLOAD_STORY_HEADER_URL, urlParameters, TAG);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Parse JSON
            try {
                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("result");
                if (status.equalsIgnoreCase("success")) {
                    afterStoryHeaderUpload();
                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getActivity(), "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception" + e.getMessage());
                Toast.makeText(getActivity(), "Parsing JSON exception: " + s,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Uploads a StoryElement to the online database. This will only occur when the final
     * version of the story is being uploaded, so if it takes multiple attempts and the
     * StoryElement is already in the database, this does not count as an error.
     */
    public class StoryElementUploadTask extends AbstractPostAsyncTask<String, Void, String> {

        String mAuthor;
        String mStoryId;
        int mElementId;

        /**
         * Starts the story element upload process.
         * @param params The story element author, story ID, element ID title, image URL,
         *               description, isEnding, choice 1 ID, choice 2 ID, choice 1 text, and
         *               choice 2 text, in that order.
         * @return A string holding the result of the request.
         */
        @Override
        protected String doInBackground(String...params) {

            mAuthor = params[0];
            mStoryId = params[1];
            mElementId = Integer.parseInt(params[2]);

            String urlParameters = "author=" + params[0]
                    + "&story_id=" + params[1]
                    + "&element_id=" + params[2]
                    + "&title=" + params[3]
                    + "&image_url=" + params[4]
                    + "&description=" + params[5]
                    + "&is_ending=" + params[6]
                    + "&choice1_id=" + params[7]
                    + "&choice2_id=" + params[8]
                    + "&choice1_text=" + params[9]
                    + "&choice2_text=" + params[10];
            try {
                return downloadUrl(UPLOAD_STORY_ELEMENT_URL, urlParameters, TAG);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Parse JSON
            try {
                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("result");
                if (status.equalsIgnoreCase("success")) {
                    afterStoryElementUpload(mAuthor, mStoryId, mElementId);
                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getActivity(), "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception" + e.getMessage());
                Toast.makeText(getActivity(), "Parsing JSON exception: " + s,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class StoryDeleteTask extends AbstractPostAsyncTask<String, Void, String> {

        /**
         * Starts the registration process.
         * @param params The story author and story ID, in that order.
         * @return A string holding the result of the request.
         */
        @Override
        protected String doInBackground(String...params) {
            String urlParameters = "author=" + params[0]
                    + "&story_id=" + params[1];
            try {
                return downloadUrl(DELETE_STORY_URL, urlParameters, TAG);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid";
            }
        }

        // No post execute: if the delete doesn't work, the user simply loses access
        // to that storyId.
    }

}
