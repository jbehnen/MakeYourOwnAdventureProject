package behnen.julia.makeyourownadventure;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import behnen.julia.makeyourownadventure.model.Story;
import behnen.julia.makeyourownadventure.model.StoryElement;
import behnen.julia.makeyourownadventure.support.AbstractStoryCheckTask;
import behnen.julia.makeyourownadventure.support.AbstractPostAsyncTask;

/**
 * Created by Julia on 11/1/2015.
 */
public class MyStoriesFragment extends Fragment {

    private static final String TAG = "MyStoriesFragment";
    private static final String UPLOAD_STORY_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/addStory.php";
    private static final String UPDATE_STORY_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/updateStory.php";

    private MyStoriesInteractionListener mCallback;
    private Button mDefaultStoryButton;
    private Story mUploadingStory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_stories, container, false);

        mDefaultStoryButton = (Button) v.findViewById(R.id.default_story_upload_button);
        mUploadingStory = null;

        mDefaultStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDefaultStoryButton.setEnabled(false);
                mUploadingStory = getDefaultStory();
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

    private void afterStoryCheck(boolean storyExists) {
        if (storyExists) {
            new StoryUpdateTask().execute(mUploadingStory.getAuthor(), mUploadingStory.getStoryId(),
                    mUploadingStory.getTitle(), mUploadingStory.getDescription(),
                    mUploadingStory.getSerializedStory());
        } else {
            new StoryUploadTask().execute(mUploadingStory.getAuthor(), mUploadingStory.getStoryId(),
                    mUploadingStory.getTitle(), mUploadingStory.getDescription(),
                    mUploadingStory.getSerializedStory());
        }
        mDefaultStoryButton.setEnabled(true);
    }

    private void attemptStoryUpload() {
        Story defaultStory = getDefaultStory();
        new StoryCheckTask().execute(defaultStory.getAuthor(), defaultStory.getStoryId());
    }

    public class StoryCheckTask extends AbstractStoryCheckTask {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Parse JSON
            try {
                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("result");
                if (status.equalsIgnoreCase("success")) {
                    afterStoryCheck(jsonObject.getBoolean("storyExists"));
                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getActivity(), "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                    mDefaultStoryButton.setEnabled(true);
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception" + e.getMessage());
                Toast.makeText(getActivity(), "Parsing JSON exception: " + s,
                        Toast.LENGTH_SHORT).show();
                mDefaultStoryButton.setEnabled(true);
            }
        }
    }

    /**
     * Represents an asynchronous task used to upload a Story.
     */
    public class StoryUploadTask extends AbstractPostAsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...params) {
            String author = params[0];
            String storyId = params[1];
            String title = params[2];
            String description = params[3];
            String serializedStory = params[4];

            String urlParameters = "author=" + author
                    + "&story_id=" + storyId
                    + "&title=" + title
                    + "&description=" + description
                    + "&serialized_story=" + serializedStory;
            try {
                return downloadUrl(UPLOAD_STORY_URL, urlParameters, TAG);
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
                    Toast.makeText(getActivity(), "Story uploaded",
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

    public class StoryUpdateTask extends AbstractPostAsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...params) {
            String author = params[0];
            String storyId = params[1];
            String title = params[2];
            String description = params[3];
            String serializedStory = params[4];

            String urlParameters = "author=" + author
                    + "&story_id=" + storyId
                    + "&title=" + title
                    + "&description=" + description
                    + "&serialized_story=" + serializedStory;
            try {
                return downloadUrl(UPDATE_STORY_URL, urlParameters, TAG);
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
                    Toast.makeText(getActivity(), "Story updated",
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

    private Story getDefaultStory() {
        Story defaultStory = new Story("tempAuthor", "default_story");
        defaultStory.setTitle("The Title of the Story");
        defaultStory.setDescription("This is the description of the story!");
        StoryElement start = new StoryElement(0, "Start of the Story",
                "/public_html/images/shared/trees_1.jpg",
                "This is the start of the story. End now or make another choice?",
                false, new int[] {1, 2}, new String[] {"End now", "Another choice"});
        StoryElement endNow = new StoryElement(1, "Start of the Story",
                "/public_html/images/shared/trees_1.jpg",
                "Thanks for making this quick.");
        StoryElement secondChoice = new StoryElement(2, "You are doomed",
                "/public_html/images/shared/trees_1.jpg",
                "Your next choice will end the game.",
                false, new int[] {3, 3}, new String[] {"End now", "Another choice"});
        StoryElement inevitableEnd = new StoryElement(3, "Start of the Story",
                "/public_html/images/shared/trees_1.jpg",
                "This was inevitable.");
        defaultStory.addStoryElement(start);
        defaultStory.addStoryElement(endNow);
        defaultStory.addStoryElement(secondChoice);
        defaultStory.addStoryElement(inevitableEnd);

        String serialized = defaultStory.getSerializedStory();
        Story story = new Story(serialized);
        Log.d(TAG, "AUTHOR: " + story.getAuthor());

        return defaultStory;
    }
}
