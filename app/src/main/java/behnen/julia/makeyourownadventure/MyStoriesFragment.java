package behnen.julia.makeyourownadventure;

import android.app.Activity;
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
import behnen.julia.makeyourownadventure.support.AbstractPostAsyncTask;

/**
 * Created by Julia on 11/1/2015.
 */
public class MyStoriesFragment extends Fragment {

    private static final String TAG = "MyStoriesFragment";
    private static final String REGISTER_STORY_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/registerStory.php";
    private static final String UPLOAD_STORY_HEADER_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/uploadStoryHeader.php";
    private static final String UPLOAD_STORY_ELEMENT_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/uploadStoryElement.php";

    private MyStoriesInteractionListener mCallback;

    private EditText mAuthorEditText;
    private EditText mStoryIdEditText;
    private Button mCreateStoryButton;
    private Button mUploadStoryButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_stories, container, false);

        mAuthorEditText = (EditText) v.findViewById(R.id.create_story_author_edit_text);
        mStoryIdEditText = (EditText) v.findViewById(R.id.create_story_story_id_edit_text);
        mCreateStoryButton = (Button) v.findViewById(R.id.story_create_button);
        mUploadStoryButton = (Button) v.findViewById(R.id.story_upload_button);

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (MyStoriesInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement MyStoriesInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface MyStoriesInteractionListener {
    }

    private void attemptStoryRegister() {
        String author = mAuthorEditText.getText().toString();
        String storyId = mStoryIdEditText.getText().toString();

        new StoryRegisterTask().execute(author, storyId);
    }

    private void attemptStoryUpload() {
        String author = mAuthorEditText.getText().toString();
        String storyId = mStoryIdEditText.getText().toString();
        StoryHeader storyHeader = getStoryHeader(author, storyId);

        new StoryHeaderUploadTask().execute(author, storyId, storyHeader.getTitle(),
                storyHeader.getDescription());

        List<StoryElement> storyElements = getStoryElements(author, storyId);

        for (StoryElement element: storyElements) {
            new StoryElementUploadTask().execute(author, storyId,
                    Integer.toString(element.getElementId()),
                    element.getTitle(), element.getImageUrl(), element.getDescription(),
                    Boolean.toString(element.isEnding()), Integer.toString(element.getChoice1Id()),
                    Integer.toString(element.getChoice2Id()),
                    element.getChoice1Text(), element.getChoice2Text());
        }
    }

    public class StoryRegisterTask extends AbstractPostAsyncTask<String, Void, String> {

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

    public class StoryHeaderUploadTask extends AbstractPostAsyncTask<String, Void, String> {

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

    public class StoryElementUploadTask extends AbstractPostAsyncTask<String, Void, String> {

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
     * Returns the StoryHeader associated with a given author and story ID in the local
     * database, or null if it doesn't exist.
     * @param author The author of the StoryHeader.
     * @param storyId The story ID of the StoryHeader.
     * @return The StoryHeader associated with a given author and story ID in the local
     * database; null if it doesn't exist.
     */
    private StoryHeader getStoryHeader(String author, String storyId) {
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
        List<StoryElement> storyElements = new ArrayList<>();
        StoryElement start = new StoryElement(author, storyId, 0, "Start of the Story",
                "trees_1.jpg",
                "This is the start of the story, " + author + ". End now or make another choice?",
                false, 1, 2, "End now", "Another choice");
        StoryElement endNow = new StoryElement(author, storyId, 1, "Start of the Story",
                "trees_1.jpg",
                "Thanks for making this quick.");
        StoryElement secondChoice = new StoryElement(author, storyId, 2, "You are doomed",
                "trees_1.jpg",
                "Your next choice will end the game.",
                false, 3, 3, "End now", "Another choice");
        StoryElement inevitableEnd = new StoryElement(author, storyId, 3, "Start of the Story",
                "trees_1.jpg",
                "This was inevitable. Thanks for testing this game, " + author + "!");
        storyElements.add(start);
        storyElements.add(endNow);
        storyElements.add(secondChoice);
        storyElements.add(inevitableEnd);

        return storyElements;
    }
}
