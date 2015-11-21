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

import behnen.julia.makeyourownadventure.model.StoryHeader;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkedStoriesFragment extends Fragment {

    private OnBookmarkedStoriesInteractionListener mCallback;

    public interface OnBookmarkedStoriesInteractionListener {
        public List<StoryHeader> onBookmarkedStoriesGetStories();
        public void onBookmarkedStoriesSelectStory(StoryHeader storyHeader);
        public void onBookmarkedStoriesAddStory();
    }

    public BookmarkedStoriesFragment() {
        // Required empty public constructor
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
                    + "must implement RegisterInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

}