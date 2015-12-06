package behnen.julia.makeyourownadventure;


import android.content.Context;
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

import behnen.julia.makeyourownadventure.asyncs.AbstractPostAsyncTask;
import behnen.julia.makeyourownadventure.model.StoryHeader;


/**
 * A fragment that allows the user to download a new story.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class GetNewStoryFragment extends Fragment {

    /**
     * A tag used for logging.
     */
    private static final String TAG = "GetNewStoryFragment";
    /**
     * The URL for story header download requests.
     */
    private static final String GET_STORY_HEADER_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/getStoryHeader.php";

    /**
     * The button used to download the story.
     */
    private Button mGetStoryButton;

    /**
     * The context which implements the interface methods.
     */
    private OnGetNewStoryInteractionListener mCallback;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnGetNewStoryInteractionListener {
        /**
         * Callback triggered when a story is downloaded and ready to
         * be saved.
         * @param storyHeader The downloaded story header.
         * @return True if the story is saved, false otherwise.
         */
        boolean onGetNewStoryAddStory(StoryHeader storyHeader);
    }

    public GetNewStoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    /**
     * Resumes fragment and updates activity action bar title.
     *
     * Action bar title update code adapted from
     * http://stackoverflow.com/questions/13472258/
     * handling-actionbar-title-with-the-fragment-back-stack
     */
    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().setTitle(R.string.get_new_story_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_new_story, container, false);

        final EditText authorEditText =
                (EditText) view.findViewById(R.id.get_new_story_author_edit_text);
        final EditText storyIdEditText =
                (EditText) view.findViewById(R.id.get_new_story_story_id_edit_text);

        mGetStoryButton = (Button) view.findViewById(R.id.get_new_story_action);
        mGetStoryButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   // Disable download button
                                                   mGetStoryButton.setEnabled(false);
                                                   String author = authorEditText.getText().toString();
                                                   String storyId = storyIdEditText.getText().toString();
                                                   new StoryGetHeaderTask().execute(author, storyId);
                                               }
                                           }
        );

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnGetNewStoryInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnGetNewStoryInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /**
     * Pops the fragment when there is a successful save.
     */
    private void onSaveSuccess() {
        getFragmentManager().popBackStackImmediate();
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
                    // enable button
                    mGetStoryButton.setEnabled(true);
                    if (mCallback != null) {
                        // If successful, try saving story header in callback.
                        StoryHeader storyHeader =
                                StoryHeader.parseJson(jsonObject.getString("storyHeader"));
                        boolean added = mCallback.onGetNewStoryAddStory(storyHeader);
                        // If story successfully saved
                        if (added) {
                            Toast.makeText(getActivity(), "Story saved",
                                    Toast.LENGTH_SHORT).show();
                            onSaveSuccess();
                        } else {
                            Toast.makeText(getActivity(), "Story could not be saved",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getActivity(), "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                    // enable button
                    mGetStoryButton.setEnabled(true);
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception" + e.getMessage());
                Toast.makeText(getActivity(),
                        getActivity().getResources().getString(R.string.async_error),
                        Toast.LENGTH_SHORT).show();
                // enable button
                mGetStoryButton.setEnabled(true);
            }
        }
    }
}
