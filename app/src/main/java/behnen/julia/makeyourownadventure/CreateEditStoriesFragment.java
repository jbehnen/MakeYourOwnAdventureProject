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
 *
 *
 * @author Julia Behnen
 * @version November 4, 2015
 */
public class CreateEditStoriesFragment extends Fragment {

    private OnCreateEditStoriesInteractionListener mCallback;

    public interface OnCreateEditStoriesInteractionListener {
        List<StoryHeader> onCreateEditStoriesGetStories();
        void onCreateEditStoriesSelectStory(StoryHeader storyHeader);
        void onCreateEditStoriesAddStory();
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
        getActivity().setTitle(R.string.create_edit_stories_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_edit_stories, container, false);
        ListView bookmarkedStories = (ListView) view.findViewById(R.id.create_edit_stories_list);
        FloatingActionButton addStoryButton =
                (FloatingActionButton) view.findViewById(R.id.create_edit_stories_add_story_action);

        bookmarkedStories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mCallback != null) {
                    StoryHeader storyHeader = (StoryHeader) adapterView.getItemAtPosition(position);
                    mCallback.onCreateEditStoriesSelectStory(storyHeader);
                }
            }
        });

        addStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onCreateEditStoriesAddStory();
                }
            }
        });

        if (mCallback != null) {
            List<StoryHeader> list = mCallback.onCreateEditStoriesGetStories();

            ListAdapter adapter = new StoryHeaderAdapter(view.getContext(), list);
            bookmarkedStories.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCreateEditStoriesInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnCreateEditStoriesInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
