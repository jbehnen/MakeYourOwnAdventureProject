package behnen.julia.makeyourownadventure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import behnen.julia.makeyourownadventure.model.StoryHeader;
import behnen.julia.makeyourownadventure.model.StoryElement;

/**
 * [PHASE I DEMO] A fragment that allows users to create and upload a new story.
 *
 * @author Julia Behnen
 * @version November 4, 2015
 */
public class CreateEditStoriesFragment extends Fragment {

    private static final String TAG = "Cre8EditStoriesFragment";
    /**
     * The URL for story registration requests.
     */
    private static final String REGISTER_STORY_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/registerStory.php";
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

    private EditText mAuthorEditText;
    private EditText mStoryIdEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_stories, container, false);

        mAuthorEditText = (EditText) v.findViewById(R.id.create_story_author_edit_text);
        mStoryIdEditText = (EditText) v.findViewById(R.id.create_story_story_id_edit_text);

        Button mCreateStoryButton = (Button) v.findViewById(R.id.story_create_button);
        Button mUploadStoryButton = (Button) v.findViewById(R.id.story_upload_button);

        mCreateStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptStoryRegister();
            }
        });
        mUploadStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptStoryUpload();
            }
        });

        return v;
    }

    private void attemptStoryRegister() {
        String author = mAuthorEditText.getText().toString();
        String storyId = mStoryIdEditText.getText().toString();

        new StoryRegisterTask().execute(author, storyId);
    }

    /**
     * Attempts to upload a story: both the story header and the story elements. The
     * story elements are uploaded first. If all of the story elements are uploaded
     * successfully, then the story header is uploaded.
     */
    private void attemptStoryUpload() {
        String author = mAuthorEditText.getText().toString();
        String storyId = mStoryIdEditText.getText().toString();
        StoryHeader storyHeader = getStoryHeader(author, storyId);
        // todo: mark story as "final" (no longer editable) in database in case
        // full upload does not work the first time
        List<StoryElement> storyElements = getStoryElements(author, storyId);

        for (StoryElement element: storyElements) {
            new StoryElementUploadTask().execute(author, storyId,
                    Integer.toString(element.getElementId()),
                    element.getTitle(), element.getImageUrl(), element.getDescription(),
                    Boolean.toString(element.isEnding()), Integer.toString(element.getChoice1Id()),
                    Integer.toString(element.getChoice2Id()),
                    element.getChoice1Text(), element.getChoice2Text());
        }

        //TODO: if all story elements deleted from local database (proxy for "uploaded"),
        // THEN and ONLY upload the story header. Then delete the story from create/edit list.
        new StoryHeaderUploadTask().execute(author, storyId, storyHeader.getTitle(),
                storyHeader.getDescription());

    }

    /**
     * Returns the StoryHeader associated with a given author and story ID in the local
     * database, or null if it doesn't exist.
     * @param author The author of the StoryHeader.
     * @param storyId The story ID of the StoryHeader.
     * @return The StoryHeader associated with a given author and story ID in the local
     * database; null if it doesn't exist.
     */
    private StoryHeader getStoryHeader(String author, String storyId) {

        // For demo purposes, a sample StoryHeader is generated.

        return new StoryHeader(author, storyId, "Title!", "Description!");
    }

    /**
     * Returns the list of StoryElement objects associated with a given author and story ID in the
     * local database, or an empty list if none exist.
     * @param author The author of the StoryElement objects.
     * @param storyId The story ID of the StoryElement objects.
     * @return The list of StoryElement objects associated with a given author and story ID in the
     * local database; an empty list if none exist.
     */
    private List<StoryElement> getStoryElements(String author, String storyId) {

        // For demo purposes, a sample List is generated

        List<StoryElement> storyElements = new ArrayList<>();
        StoryElement start = new StoryElement(author, storyId, 0, "Start of the Story",
                "trees_1.jpg",
                "This is the start of the story, " + author + ". End now or make another choice?",
                1, 2, "End now", "Another choice");
        StoryElement endNow = new StoryElement(author, storyId, 1, "Start of the Story",
                "trees_1.jpg",
                "Thanks for making this quick.");
        StoryElement secondChoice = new StoryElement(author, storyId, 2, "You are doomed",
                "trees_1.jpg",
                "Your next choice will end the game.",
                3, 3, "End now", "Another choice");
        StoryElement inevitableEnd = new StoryElement(author, storyId, 3, "Start of the Story",
                "trees_1.jpg",
                "This was inevitable. Thanks for testing this game, " + author + "!");
        storyElements.add(start);
        storyElements.add(endNow);
        storyElements.add(secondChoice);
        storyElements.add(inevitableEnd);

        return storyElements;
    }

    /**
     * Registers a story in the database. Since author and story ID are primary keys
     * for identifying a story both locally and in the online database, a user registers
     * them with the online database in a StoryHeader when a story is first created to make
     * sure that the later upload of the full story will be successful. Registration
     * StoryHeaders can be identified by their null title and description.
     */
    public class StoryRegisterTask extends AbstractPostAsyncTask<String, Void, String> {

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
                return downloadUrl(REGISTER_STORY_URL, urlParameters, TAG);
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
                    Toast.makeText(getActivity(), "Story registered",
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
