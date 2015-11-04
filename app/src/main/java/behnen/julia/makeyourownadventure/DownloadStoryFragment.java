package behnen.julia.makeyourownadventure;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import behnen.julia.makeyourownadventure.model.StoryElement;
import behnen.julia.makeyourownadventure.model.StoryHeader;
import behnen.julia.makeyourownadventure.support.AbstractPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDownloadStoryInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DownloadStoryFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class DownloadStoryFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "DownloadStoryFragment";
    private static final String GET_STORY_HEADER_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/getStoryHeader.php";
    private static final String GET_STORY_ELEMENT_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/getStoryElement.php";
    private static final String SHARED_IMAGES_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/images/shared/";

    EditText mAuthorEditText;
    EditText mStoryIdEditText;

    Button mDownloadStoryButton;
    Button mPlayStoryButton;

    TextView mDownloadedStoryAuthor;
    TextView mDownloadedStoryStoryId;
    TextView mDownloadedStoryTitle;
    TextView mDownloadedStoryDescription;

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

    ImageView mDownloadedElementImage;

    StoryHeader mStoryHeader;

    private OnDownloadStoryInteractionListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download_story, container, false);

        mAuthorEditText = (EditText) view.findViewById(R.id.download_author_edit_text);
        mStoryIdEditText = (EditText) view.findViewById(R.id.download_story_id_edit_text);
        mDownloadStoryButton = (Button) view.findViewById(R.id.download_story_button);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnDownloadStoryInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDownloadStoryInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void downloadHeaderSuccess(StoryHeader storyHeader) {
        if (storyHeader != null) {
            mDownloadedStoryAuthor.setText(storyHeader.getAuthor());
            mDownloadedStoryStoryId.setText(storyHeader.getStoryId());
            mDownloadedStoryTitle.setText(storyHeader.getTitle());
            mDownloadedStoryDescription.setText(storyHeader.getDescription());
        }
    }

    private void downloadElementSuccess(StoryElement storyElement) {
        if (storyElement != null) {

            new DownloadImageTask().execute(SHARED_IMAGES_URL + storyElement.getImageUrl());

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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDownloadStoryInteractionListener {
    }

    public class StoryGetHeaderTask extends AbstractPostAsyncTask<String, Void, String> {

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
     * Represents an asynchronous task used to upload a Story.
     */
    public class StoryGetElementTask extends AbstractPostAsyncTask<String, Void, String> {

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
                    if (mCallback != null) {
                        String elementString = jsonObject.getString("storyElement");
                        StoryElement element = StoryElement.parseJson(elementString);
                        Toast.makeText(getActivity(), "Success",
                                Toast.LENGTH_SHORT).show();
                        downloadElementSuccess(element);
                    }

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

    /**
     * Running the loading of the JSON in a separate thread.
     * Code adapted from http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
     * Code re-adapted from CSSAppWithFragments
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    InputStream is = new BufferedInputStream(urlObject.openStream());
                    bitmap = BitmapFactory.decodeStream(is);

                } catch (Exception e) {
                    String response = "Unable to download the image, Reason: "
                            + e.getMessage();
                }
            }
            return bitmap;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. If there was an exception, it is displayed in red using the text
         * view widget. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         * @param bitmap
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mDownloadedElementImage.setImageBitmap(bitmap);
        }
    }

}
