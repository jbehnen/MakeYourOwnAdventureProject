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

import behnen.julia.makeyourownadventure.adapters.StoryElementAdapter;
import behnen.julia.makeyourownadventure.model.StoryElement;


/**
 * A fragment that displays the story elements for a given story
 * that the user has created.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class CreatedStoryElementsFragment extends Fragment {

    /**
     * The tag used to identify the author argument in the bundle.
     */
    private static final String AUTHOR = "Author";
    /**
     * The tag used to identify the storyId argument in the bundle.
     */
    private static final String STORY_ID = "StoryId";

    /**
     * The context which implements the interface methods.
     */
    private OnCreatedStoryElementsInteractionListener mCallback;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnCreatedStoryElementsInteractionListener {
        /**
         * Callback triggered when the fragment first loads; returns all story elements that the
         * user has created for the current story.
         * @return All story elements that the user has created for the current story
         */
        List<StoryElement> onCreatedStoryElementsGetElements(String author, String storyId);
        /**
         * Callback triggered when the user selects a story element.
         * @param storyElement The story element that the user selects.
         */
        void onCreatedStoryElementsSelectElement(StoryElement storyElement);
        /**
         * Callback triggered when the user wants to create a new element.
         */
        void onCreatedStoryElementsAddElement(String author, String storyId);
    }

    /**
     * DO NOT USE. Only create the fragment using the given newInstance method.
     */
    public CreatedStoryElementsFragment() {

    }

    /**
     * Creates a new instance of CreatedStoryElementsFragment.
     * @param author The author of the story elements.
     * @param storyId The storyId of the story elements.
     * @return A new instance of CreatedStoryElementsFragment.
     */
    public static CreatedStoryElementsFragment newInstance(String author, String storyId) {
        if (author == null || storyId == null) {
            throw new IllegalArgumentException();
        }
        Bundle args = new Bundle();
        args.putString(AUTHOR, author);
        args.putString(STORY_ID, storyId);

        CreatedStoryElementsFragment fragment = new CreatedStoryElementsFragment();
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
        getActivity().setTitle(R.string.created_story_elements_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_created_story_elements, container, false);
        ListView bookmarkedStories = (ListView) view.findViewById(R.id.created_story_elements_list);
        FloatingActionButton addStoryButton =
                (FloatingActionButton) view.findViewById(R.id.created_story_elements_add_element_action);

        final String author = getArguments().getString(AUTHOR);
        final String storyId = getArguments().getString(STORY_ID);

        bookmarkedStories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mCallback != null) {
                    StoryElement storyElement = (StoryElement) adapterView.getItemAtPosition(position);
                    mCallback.onCreatedStoryElementsSelectElement(storyElement);
                }
            }
        });

        addStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onCreatedStoryElementsAddElement(author, storyId);
                }
            }
        });

        if (mCallback != null) {
            List<StoryElement> list = mCallback.onCreatedStoryElementsGetElements(author, storyId);

            ListAdapter adapter = new StoryElementAdapter(view.getContext(), list);
            bookmarkedStories.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCreatedStoryElementsInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnCreatedStoryElementsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

}
