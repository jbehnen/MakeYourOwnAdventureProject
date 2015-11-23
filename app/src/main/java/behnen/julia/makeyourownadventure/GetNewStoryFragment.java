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
 * A simple {@link Fragment} subclass.
 */
public class GetNewStoryFragment extends Fragment {

    private static final String TAG = "GetNewStoryFragment";
    /**
     * The URL for story header download requests.
     */
    private static final String GET_STORY_HEADER_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/getStoryHeader.php";

    private Button mGetStoryButton;

    private OnGetNewStoryInteractionListener mCallback;

    public interface OnGetNewStoryInteractionListener {
        public boolean onGetNewStoryAddStory(StoryHeader storyHeader);
    }

    public GetNewStoryFragment() {
        // Required empty public constructor
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
                    + "must implement OnSignInInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
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
                    mGetStoryButton.setEnabled(true);
                    if (mCallback != null) {
                        StoryHeader storyHeader =
                                StoryHeader.parseJson(jsonObject.getString("storyHeader"));
                        boolean added = mCallback.onGetNewStoryAddStory(storyHeader);
                        if (added) {
                            Toast.makeText(getActivity(), "Story bookmarked",
                                    Toast.LENGTH_SHORT).show();
                            getFragmentManager().popBackStackImmediate();
                        } else {
                            // TODO: get error code and specify why (eg existing story)
                            Toast.makeText(getActivity(), "Story could not be saved",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getActivity(), "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                    mGetStoryButton.setEnabled(true);
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception" + e.getMessage());
                Toast.makeText(getActivity(), "Parsing JSON exception: " + s,
                        Toast.LENGTH_SHORT).show();
                mGetStoryButton.setEnabled(true);
            }
        }
    }
}
