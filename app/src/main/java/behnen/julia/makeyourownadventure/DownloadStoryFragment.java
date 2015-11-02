package behnen.julia.makeyourownadventure;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import behnen.julia.makeyourownadventure.model.Story;
import behnen.julia.makeyourownadventure.support.AbstractPostAsyncTask;
import behnen.julia.makeyourownadventure.support.AbstractStoryCheckTask;


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
    private static final String GET_STORY_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/getStory.php";

    EditText mAuthorEditText;
    EditText mStoryIdEditText;
    Button mDownloadStoryButton;
    String mDownloadingAuthor;
    String mDownloadingStoryId;

    private OnDownloadStoryInteractionListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download_story, container, false);

        mAuthorEditText = (EditText) view.findViewById(R.id.download_author_edit_text);
        mStoryIdEditText = (EditText) view.findViewById(R.id.download_story_id_edit_text);
        mDownloadStoryButton = (Button) view.findViewById(R.id.download_story_button);

        mDownloadStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownloadStoryButton.setEnabled(false);
                mDownloadingAuthor = mAuthorEditText.getText().toString();
                mDownloadingStoryId = mStoryIdEditText.getText().toString();

                new StoryCheckTask().execute(mDownloadingAuthor, mDownloadingStoryId);
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

    private void afterStoryCheck(boolean storyExists) {
        if (storyExists) {
            new StoryGetTask().execute(mDownloadingAuthor, mDownloadingStoryId);
        } else {
            Toast.makeText(getActivity(), "Story not in database",
                    Toast.LENGTH_SHORT).show();
        }
        mDownloadStoryButton.setEnabled(true);
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
        void onDownloadStoryDownloadSuccess(String serializedStory);
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
    public class StoryGetTask extends AbstractPostAsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...params) {
            String author = params[0];
            String storyId = params[1];

            String urlParameters = "author=" + author
                    + "&story_id=" + storyId;
            try {
                return downloadUrl(GET_STORY_URL, urlParameters, TAG);
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
                    Toast.makeText(getActivity(), "Story downloaded",
                            Toast.LENGTH_SHORT).show();
                    if (mCallback != null) {
                        mCallback.onDownloadStoryDownloadSuccess(jsonObject
                                .getString("serialized_story"));
                    }

                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getActivity(), "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception: " + e.getMessage());
                Toast.makeText(getActivity(), "Parsing JSON exception",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
