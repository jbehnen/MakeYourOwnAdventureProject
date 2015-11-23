package behnen.julia.makeyourownadventure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import behnen.julia.makeyourownadventure.model.StoryElement;
import behnen.julia.makeyourownadventure.model.StoryHeader;

/**
 * [PHASE I DEMO] A fragment that allows users to download a story and view its first element.
 *
 * @author Julia Behnen
 * @version November 4, 2015
 */
public class DownloadStoryFragment extends Fragment {

    private static final String TAG = "DownloadStoryFragment";
    /**
     * The URL for story header download requests.
     */
    private static final String GET_STORY_HEADER_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/getStoryHeader.php";
    /**
     * The URL for story element download requests.
     */
    private static final String GET_STORY_ELEMENT_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/getStoryElement.php";
    /**
     * The URL for story element download requests from the shared image directory.
     */
    private static final String SHARED_IMAGES_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/images/shared/";

    EditText mAuthorEditText;
    EditText mStoryIdEditText;

    Button mDownloadStoryButton;
    Button mPlayStoryButton;

    /**
     * The following are temporary variables for demonstration purposes. They all reference
     * TextViews used to display the downloaded data for the requested story header.
     */
    TextView mDownloadedStoryAuthor;
    TextView mDownloadedStoryStoryId;
    TextView mDownloadedStoryTitle;
    TextView mDownloadedStoryDescription;

    /**
     * The following are temporary variables for demonstration purposes. They all reference
     * TextViews used to display the downloaded data for the requested story element.
     */
    TextView mDownloadedElementAuthor;
    TextView mDownloadedElementStoryId;
    TextView mDownloadedElementElementId;
    TextView mDownloadedElementTitle;
    TextView mDownloadedElementImageUrl;
    TextView mDownloadedElementDescription;
    TextView mDownloadedElementIsEnding;
    TextView mDownloadedElementChoice1Id;
    TextView mDownloadedElementChoice2Id;
    TextView mDownloadedElementChoice1Text;
    TextView mDownloadedElementChoice2Text;

    /**
     * Temporary variable for demonstration purposes.
     * The ImageView used to display the downloaded image for the requested story element.
     */
    ImageView mDownloadedElementImage;

    /**
     * Temporary variable for demonstration purposes.
     * Holds the downloaded story header so that its first story element can be downloaded.
     * Would normally be loaded from the database upon request from a ListView of all
     * downloaded stories in another fragment.
     */
    StoryHeader mStoryHeader;

    /**
     * The context which implements the interface methods.
     */
    private DownloadStoryInteractionListener mCallback;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface DownloadStoryInteractionListener {
        // TODO move method to new home
        //public void downloadStory
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download_story, container, false);

        mPlayStoryButton = (Button) view.findViewById(R.id.download_story_play_story_button);

        mDownloadedStoryAuthor = (TextView) view.findViewById(R.id.download_downloaded_author);
        mDownloadedStoryStoryId = (TextView) view.findViewById(R.id.download_downloaded_story_id);
        mDownloadedStoryTitle = (TextView) view.findViewById(R.id.download_downloaded_title);
        mDownloadedStoryDescription =
                (TextView) view.findViewById(R.id.download_downloaded_description);

        mDownloadedElementAuthor =
                (TextView) view.findViewById(R.id.download_element_author);
        mDownloadedElementStoryId =
                (TextView) view.findViewById(R.id.download_element_story_id);
        mDownloadedElementElementId =
                (TextView) view.findViewById(R.id.download_element_element_id);
        mDownloadedElementTitle =
                (TextView) view.findViewById(R.id.download_element_title);
        mDownloadedElementImageUrl =
                (TextView) view.findViewById(R.id.download_element_image_url);
        mDownloadedElementDescription =
                (TextView) view.findViewById(R.id.download_element_description);
        mDownloadedElementIsEnding =
                (TextView) view.findViewById(R.id.download_element_is_ending);
        mDownloadedElementChoice1Id =
                (TextView) view.findViewById(R.id.download_element_choice1_id);
        mDownloadedElementChoice2Id =
                (TextView) view.findViewById(R.id.download_element_choice2_id);
        mDownloadedElementChoice1Text =
                (TextView) view.findViewById(R.id.download_element_choice1_text);
        mDownloadedElementChoice2Text =
                (TextView) view.findViewById(R.id.download_element_choice2_text);

        mDownloadedElementImage = (ImageView) view.findViewById(R.id.story_element_image);

        mStoryHeader = null;

        mDownloadStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String author = mAuthorEditText.getText().toString();
                String storyId = mStoryIdEditText.getText().toString();

                new StoryGetHeaderTask().execute(author, storyId);
            }
        });

        mPlayStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStoryHeader != null) {
                    String author = mStoryHeader.getAuthor();
                    String storyId = mStoryHeader.getStoryId();
                    new StoryGetElementTask().execute(author, storyId,
                            Integer.toString(StoryElement.START_ID));
                }
            }
        });

        return view;
    }

    /**
     * Sets the display text to show the data from the StoryHeader.
     * @param storyHeader The StoryHeader that was downloaded.
     */
    private void downloadHeaderSuccess(StoryHeader storyHeader) {
        if (storyHeader != null) {
            mDownloadedStoryAuthor.setText(storyHeader.getAuthor());
            mDownloadedStoryStoryId.setText(storyHeader.getStoryId());
            mDownloadedStoryTitle.setText(storyHeader.getTitle());
            mDownloadedStoryDescription.setText(storyHeader.getDescription());
        }
    }

    /**
     * Sets the display text to show the data from the StoryElement.
     * @param storyElement The StoryElement that was downloaded.
     */
    private void downloadElementSuccess(StoryElement storyElement) {
        if (storyElement != null) {

//            new DownloadImageTask().execute(SHARED_IMAGES_URL + storyElement.getImageUrl());

            mDownloadedElementAuthor.setText(storyElement.getAuthor());
            mDownloadedElementStoryId.setText(storyElement.getStoryId());
            mDownloadedElementElementId.setText(Integer.toString(storyElement.getElementId()));
            mDownloadedElementTitle.setText(storyElement.getTitle());
            mDownloadedElementImageUrl.setText(storyElement.getImageUrl());
            mDownloadedElementDescription.setText(storyElement.getDescription());
            mDownloadedElementIsEnding.setText(Boolean.toString(storyElement.isEnding()));
            mDownloadedElementChoice1Id.setText(Integer.toString(storyElement.getChoice1Id()));
            mDownloadedElementChoice2Id.setText(Integer.toString(storyElement.getChoice2Id()));
            mDownloadedElementChoice1Text.setText(storyElement.getChoice1Text());
            mDownloadedElementChoice2Text.setText(storyElement.getChoice2Text());
        }
    }

    /**
     * Downloads a StoryHeader from the online database.
     */
    public class StoryGetHeaderTask extends AbstractPostAsyncTask<String, Void, String> {

        /**
         * Starts the story header retrieval process.
         * @param params The story header author and story ID, in that order.
         * @return A string holding the result of the request.
         */
        @Override
        protected String doInBackground(String...params) {
            final String author = params[0];
            final String storyId = params[1];

            String urlParameters = "author=" + author
                    + "&story_id=" + storyId;
            try {
                return downloadUrl(GET_STORY_HEADER_URL, urlParameters, TAG);
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
                    mStoryHeader = StoryHeader.parseJson(jsonObject.getString("storyHeader"));
                    downloadHeaderSuccess(mStoryHeader);
                    Toast.makeText(getActivity(), "Success",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getActivity(), "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                    mDownloadStoryButton.setEnabled(true);
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception" + e.getMessage());
                Toast.makeText(getActivity(), "Parsing JSON exception: " + s,
                        Toast.LENGTH_SHORT).show();
                mDownloadStoryButton.setEnabled(true);
            }
        }
    }

    /**
     * Downloads a StoryElement from the online database.
     */
    public class StoryGetElementTask extends AbstractPostAsyncTask<String, Void, String> {

        /**
         * Starts the story header retrieval process.
         * @param params The story header author, story ID, and element ID, in that order.
         * @return A string holding the result of the request.
         */
        @Override
        protected String doInBackground(String...params) {
            String author = params[0];
            String storyId = params[1];
            String elementId = params[2];

            String urlParameters = "author=" + author
                    + "&story_id=" + storyId
                    + "&element_id=" + elementId;
            try {
                return downloadUrl(GET_STORY_ELEMENT_URL, urlParameters, TAG);
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
                    String elementString = jsonObject.getString("storyElement");
                    StoryElement element = StoryElement.parseJson(elementString);
                    Toast.makeText(getActivity(), "Success",
                            Toast.LENGTH_SHORT).show();
                    downloadElementSuccess(element);

                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getActivity(), "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), "Parsing JSON exception",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

//    /**
//     * An asynchronous task for downloading a story element image.
//     */
//    private class DownloadImageTask extends AbstractDownloadImageTask {
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            mDownloadedElementImage.setImageBitmap(bitmap);
//        }
//    }

}
