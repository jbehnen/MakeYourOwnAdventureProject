package behnen.julia.makeyourownadventure;

import android.content.Context;
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

import behnen.julia.makeyourownadventure.asyncs.AbstractPostAsyncTask;
import behnen.julia.makeyourownadventure.dialogs.EditStoryHeaderDialogFragment;
import behnen.julia.makeyourownadventure.model.StoryHeader;

/**
 * Created by Julia on 11/22/2015.
 */
public class CreatedStoryOverviewFragment extends Fragment {

    private static final String TAG = "CreatedStoryOverview";

    private static final String ARG_STORY_HEADER = "storyHeader";

    public static final int REQUEST_HEADER = 0;

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

    private OnCreatedStoryOverviewInteractionListener mCallback;

    private StoryHeader mStoryHeader;

    public interface OnCreatedStoryOverviewInteractionListener {
        void onCreatedStoryOverviewFragmentEditHeader(StoryHeader storyHeader);
        void onCreatedStoryOverviewFragmentEditElements(String author, String storyId);
        void onCreatedStoryOverviewFragmentPlayStory(String author, String storyId);
        boolean onCreatedStoryOverviewFragmentDeleteStory(String author, String storyId);
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

        final Button editStoryHeaderButton =
                (Button) view.findViewById(R.id.created_story_overview_edit_story_info_button);
        editStoryHeaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditStoryHeaderDialogFragment fragment = EditStoryHeaderDialogFragment.newInstance(
                        mStoryHeader.getTitle(), mStoryHeader.getDescription());
                fragment.setTargetFragment(CreatedStoryOverviewFragment.this, REQUEST_HEADER);
                fragment.show(getFragmentManager(), "editStoryHeader");
            }
        });

        final Button editStoryElementsButton =
                (Button) view.findViewById(R.id.created_story_overview_edit_story_elements_button);
        editStoryElementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onCreatedStoryOverviewFragmentEditElements(
                            mStoryHeader.getAuthor(), mStoryHeader.getStoryId());
                }
            }
        });

        Button playButton = (Button) view.findViewById(R.id.created_story_overview_play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onCreatedStoryOverviewFragmentPlayStory(mStoryHeader.getAuthor(),
                            mStoryHeader.getStoryId());
                }
            }
        });

        Button deleteButton = (Button) view.findViewById(R.id.created_story_overview_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO also delete from online database
                if (mCallback != null) {
                    boolean deleted =
                            mCallback.onCreatedStoryOverviewFragmentDeleteStory(
                                    mStoryHeader.getAuthor(), mStoryHeader.getStoryId());
                    if (deleted) {
                        getFragmentManager().popBackStackImmediate();
                    } else {
                        Toast.makeText(getActivity(), "Deletion failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button uploadButton = (Button) view.findViewById(R.id.created_story_overview_upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    attemptStoryUpload();
                }
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

    /**
     * Attempts to upload a story: both the story header and the story elements. The
     * story elements are uploaded first. If all of the story elements are uploaded
     * successfully, then the story header is uploaded.
     */
    private void attemptStoryUpload() {
        // todo: mark story as "final" (no longer editable) in database in case
        // full upload does not work the first time
        //List<StoryElement> storyElements = getStoryElements(author, storyId);

        // todo: upload story elements
//        for (StoryElement element: storyElements) {
//            new StoryElementUploadTask().execute(author, storyId,
//                    Integer.toString(element.getElementId()),
//                    element.getTitle(), element.getImageUrl(), element.getDescription(),
//                    Boolean.toString(element.isEnding()), Integer.toString(element.getChoice1Id()),
//                    Integer.toString(element.getChoice2Id()),
//                    element.getChoice1Text(), element.getChoice2Text());
//        }

        //TODO: if all story elements deleted from local database (proxy for "uploaded"),
        // THEN and ONLY upload the story header. Then delete the story from create/edit list.
        new StoryHeaderUploadTask().execute(mStoryHeader.getAuthor(), mStoryHeader.getStoryId(),
                mStoryHeader.getTitle(), mStoryHeader.getDescription());
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
                    Toast.makeText(getActivity(), "Story header uploaded",
                            Toast.LENGTH_SHORT).show();
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

        /**
         * Starts the story element upload process.
         * @param params The story element author, story ID, element ID title, image URL,
         *               description, isEnding, choice 1 ID, choice 2 ID, choice 1 text, and
         *               choice 2 text, in that order.
         * @return A string holding the result of the request.
         */
        @Override
        protected String doInBackground(String...params) {

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
                    Toast.makeText(getActivity(), "Story element uploaded",
                            Toast.LENGTH_SHORT).show();
                    // TODO: delete element from local database after successful upload. Then,
                    // once all of the uploads have been attempted (or a set period of time),
                    // check the database and see if any elements are remaining. If none are
                    // left, delete the story header from local storage (give user a head's up
                    // first, let them move it to bookmarked stories).
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

}
