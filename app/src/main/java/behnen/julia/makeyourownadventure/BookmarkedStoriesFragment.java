package behnen.julia.makeyourownadventure;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import behnen.julia.makeyourownadventure.adapters.StoryHeaderAdapter;
import behnen.julia.makeyourownadventure.model.StoryHeader;


/**
 * A fragment that displays the stories that the user has downloaded and allows
 * them to add a new one.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class BookmarkedStoriesFragment extends Fragment {

    /**
     * The context which implements the interface methods.
     */
    private OnBookmarkedStoriesInteractionListener mCallback;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnBookmarkedStoriesInteractionListener {
        /**
         * Callback triggered when the fragment first loads; returns all stories that the
         * user has saved.
         * @return All stories that the user has downloaded.
         */
        List<StoryHeader> onBookmarkedStoriesGetStories();

        /**
         * Callback triggered when the user selects a story.
         * @param storyHeader The story header that the user selects.
         */
        void onBookmarkedStoriesSelectStory(StoryHeader storyHeader);

        /**
         * Callback triggered when the user wants to save a new story.
         */
        void onBookmarkedStoriesAddStory();
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
        getActivity().setTitle(R.string.bookmarked_stories_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarked_stories, container, false);
        ListView bookmarkedStories = (ListView) view.findViewById(R.id.bookmarked_stories_list);
        FloatingActionButton addStoryButton =
                (FloatingActionButton) view.findViewById(R.id.bookmarked_stories_add_story_action);

        bookmarkedStories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mCallback != null) {
                    StoryHeader storyHeader = (StoryHeader) adapterView.getItemAtPosition(position);
                    mCallback.onBookmarkedStoriesSelectStory(storyHeader);
                }
            }
        });

        addStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onBookmarkedStoriesAddStory();
                }
            }
        });

        if (mCallback != null) {
            List<StoryHeader> list = mCallback.onBookmarkedStoriesGetStories();

            ListAdapter adapter = new StoryHeaderAdapter(view.getContext(), list);
            bookmarkedStories.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnBookmarkedStoriesInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnBookmarkedStoriesInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

}
