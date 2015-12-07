package behnen.julia.makeyourownadventure;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import behnen.julia.makeyourownadventure.model.StoryHeader;

/**
 * A fragment that displays an overview of a downloaded story and allows the
 * user to play it or delete it.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class StoryOverviewFragment extends Fragment {

    /**
     * The tag used to identify the story header argument in the bundle.
     */
    private static final String ARG_STORY_HEADER = "story_header";

    /**
     * The context which implements the interface methods.
     */
    private OnStoryOverviewInteractionListener mCallback;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnStoryOverviewInteractionListener {
        /**
         * Callback triggered when the user wants to play the story.
         * @param author The author of the story.
         * @param storyId The storyId of the story.
         * @param title The title of the story.
         */
        void onStoryOverviewPlayStory(String author, String storyId, String title);

        /**
         * Callback triggered when the user wants to delete the story.
         * @param author The author of the story.
         * @param storyId The storyId of the story.
         * @return True if the story is deleted in the callback, false otherwise.
         */
        boolean onStoryOverviewDeleteStory(String author, String storyId);
    }

    /**
     * DO NOT USE. Only create the fragment using the given newInstance method.
     */
    public StoryOverviewFragment() {

    }

    /**
     * Creates a new instance of StoryOverviewFragment.
     * @param storyHeader The story being displayed.
     * @return A new instance of StoryOverviewFragment.
     */
    public static StoryOverviewFragment newInstance(StoryHeader storyHeader) {
        if (storyHeader == null) {
            throw new IllegalArgumentException();
        }
        Bundle args = new Bundle();
        args.putSerializable(ARG_STORY_HEADER, storyHeader.toString());

        StoryOverviewFragment fragment = new StoryOverviewFragment();
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
        getActivity().setTitle(R.string.story_overview_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_story_overview, container, false);

        final StoryHeader storyHeader =
                StoryHeader.parseJson((String) getArguments().getSerializable(ARG_STORY_HEADER));

        displayStoryInfo(view, storyHeader);

        Button playButton = (Button) view.findViewById(R.id.story_overview_play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onStoryOverviewPlayStory(storyHeader.getAuthor(),
                            storyHeader.getStoryId(), storyHeader.getTitle());
                }
            }
        });

        Button deleteButton = (Button) view.findViewById(R.id.story_overview_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    boolean deleted = mCallback.onStoryOverviewDeleteStory(
                                    storyHeader.getAuthor(), storyHeader.getStoryId());
                    if (deleted) {
                        getFragmentManager().popBackStackImmediate();
                    } else {
                        Toast.makeText(getActivity(), "Deletion failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    /**
     * Displays the story header info.
     * @param view The view being updated.
     * @param storyHeader The story header being displayed.
     */
    private void displayStoryInfo(View view, StoryHeader storyHeader) {
        if (storyHeader != null) {
            TextView author = (TextView) view.findViewById(R.id.story_overview_author);
            TextView storyId = (TextView) view.findViewById(R.id.story_overview_story_id);
            TextView title = (TextView) view.findViewById(R.id.story_overview_title);
            TextView description = (TextView) view.findViewById(R.id.story_overview_description);

            author.setText(storyHeader.getAuthor());
            storyId.setText(storyHeader.getStoryId());
            title.setText(storyHeader.getTitle());
            description.setText(storyHeader.getDescription());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStoryOverviewInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnStoryOverviewInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
