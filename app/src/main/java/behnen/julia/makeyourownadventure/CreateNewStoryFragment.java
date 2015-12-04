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
public class CreateNewStoryFragment extends Fragment {

    private static final String TAG = "CreateNewStory";

    /**
     * The URL for story registration requests.
     */
    private static final String REGISTER_STORY_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/registerStory.php";

    private static final String AUTHOR = "author";

    private EditText mStoryIdEditText;
    private EditText mTitleEditText;
    private EditText mDescriptionEditText;

    private String mAuthor;

    private String mCurrentStoryId;
    private String mCurrentTitle;
    private String mCurrentDescription;

    private OnCreateNewStoryInteractionListener mCallback;

    public interface OnCreateNewStoryInteractionListener {
        void onCreateNewStorySaveLocalCopy(StoryHeader storyHeader);
    }

    public static CreateNewStoryFragment newInstance(String author) {
        Bundle args = new Bundle();
        args.putString(AUTHOR, author);

        CreateNewStoryFragment fragment = new CreateNewStoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_create_new_story, container, false);

        mStoryIdEditText = (EditText) view.findViewById(R.id.create_new_story_story_id);
        mTitleEditText = (EditText) view.findViewById(R.id.create_new_story_title);
        mDescriptionEditText = (EditText) view.findViewById(R.id.create_new_story_description);

        // TODO test for not-null
        mAuthor = getArguments().getString(AUTHOR);

        Button createStoryButton = (Button) view.findViewById(R.id.create_new_story_button);
        createStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentStoryId = mStoryIdEditText.getText().toString();
                mCurrentTitle = mTitleEditText.getText().toString();
                mCurrentDescription = mDescriptionEditText.getText().toString();

                new StoryRegisterTask().execute(mAuthor, mCurrentStoryId);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCreateNewStoryInteractionListener) context;
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
                    if (mCallback != null) {
                        mCallback.onCreateNewStorySaveLocalCopy(new StoryHeader(mAuthor,
                                mCurrentStoryId,
                                mCurrentTitle, mCurrentDescription));
                        getFragmentManager().popBackStackImmediate();
                    }
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
