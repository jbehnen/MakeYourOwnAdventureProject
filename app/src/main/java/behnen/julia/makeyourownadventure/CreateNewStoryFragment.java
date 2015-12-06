package behnen.julia.makeyourownadventure;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

    /**
     * The tag used for logging.
     */
    private static final String TAG = "CreateNewStory";

    /**
     * The URL for story registration requests.
     */
    private static final String REGISTER_STORY_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/registerStory.php";

    private static final String AUTHOR = "author";

    /**
     * The author of the story.
     */
    private String mAuthor;

    /**
     * The EditTexts used for entering information.
     */
    private EditText mStoryIdEditText;
    private EditText mTitleEditText;
    private EditText mDescriptionEditText;

    /**
     * The context which implements the interface methods.
     */
    private OnCreateNewStoryInteractionListener mCallback;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnCreateNewStoryInteractionListener {
        /**
         * Callback triggered when the user saves the story.
         * @param storyHeader The story header being saved.
         */
        void onCreateNewStorySaveLocalCopy(StoryHeader storyHeader);
    }

    /**
     * DO NOT USE. Only create the fragment using the given newInstance method.
     */
    public CreateNewStoryFragment() {

    }

    /**
     * Creates a new instance of CreateNewStoryFragment.
     * @param author The author of the story.
     * @return A new instance of CreateNewStoryFragment.
     */
    public static CreateNewStoryFragment newInstance(String author) {
        if (author == null) {
            throw new IllegalArgumentException();
        }
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
        getActivity().setTitle(R.string.create_new_story_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_new_story, container, false);

        mStoryIdEditText = (EditText) view.findViewById(R.id.create_new_story_story_id);
        mTitleEditText = (EditText) view.findViewById(R.id.create_new_story_title);
        mDescriptionEditText = (EditText) view.findViewById(R.id.create_new_story_description);

        mAuthor = getArguments().getString(AUTHOR);

        Button createStoryButton = (Button) view.findViewById(R.id.create_new_story_button);
        createStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegisterStory();
            }
        });

        return view;
    }

    /**
     * Attempts to register the story (author and storyId) in the online database.
     */
    public void attemptRegisterStory() {
        boolean cancel = false;
        View focusView = null;

        String storyId = mStoryIdEditText.getText().toString();
        String title = mTitleEditText.getText().toString();
        String description = mDescriptionEditText.getText().toString();

        // Check for valid storyID
        if (TextUtils.isEmpty(storyId)) {
            mStoryIdEditText.setError(getString(R.string.error_field_required));
            focusView = mStoryIdEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        } else {
            // Registers the story
            new StoryRegisterTask(storyId, title, description).execute(mAuthor);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCreateNewStoryInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnCreateNewStoryInteractionListener");
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

        String mStoryId;
        String mTitle;
        String mDescription;

        public StoryRegisterTask(String storyId, String title, String description) {
            mStoryId = storyId;
            mTitle = title;
            mDescription = description;
        }

        /**
         * Starts the registration process.
         * @param params The story author and storyId in that order.
         * @return A string holding the result of the request.
         */
        @Override
        protected String doInBackground(String...params) {
            String urlParameters = "author=" + params[0]
                    + "&story_id=" + mStoryId;
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
                    if (mCallback != null) {
                        mCallback.onCreateNewStorySaveLocalCopy(new StoryHeader(
                                mAuthor, mStoryId, mTitle, mDescription));
                    }
                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getActivity(), "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception" + e.getMessage());
                Toast.makeText(getActivity(),
                        getActivity().getResources().getString(R.string.async_error),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
