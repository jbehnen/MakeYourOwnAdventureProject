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
 * A simple {@link Fragment} subclass.
 */
public class CreatedStoryElementsFragment extends Fragment {

    private static final String AUTHOR = "Author";
    private static final String STORY_ID = "StoryId";

    private OnCreatedStoryElementsInteractionListener mCallback;

    public interface OnCreatedStoryElementsInteractionListener {
        List<StoryElement> onCreatedStoryElementsGetElements(String author, String storyId);
        void onCreatedStoryElementsSelectElement(StoryElement storyElement);
        void onCreatedStoryElementsAddElement();
    }

    public static CreatedStoryElementsFragment newInstance(String author, String storyId) {
        Bundle args = new Bundle();
        args.putString(AUTHOR, author);
        args.putString(STORY_ID, storyId);

        CreatedStoryElementsFragment fragment = new CreatedStoryElementsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_created_story_elements, container, false);
        ListView bookmarkedStories = (ListView) view.findViewById(R.id.created_story_elements_list);
        FloatingActionButton addStoryButton =
                (FloatingActionButton) view.findViewById(R.id.created_story_elements_add_element_action);

        // TODO: check for valid arguments
        String author = getArguments().getString(AUTHOR);
        String storyId = getArguments().getString(STORY_ID);

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
                    mCallback.onCreatedStoryElementsAddElement();
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
                    + "must implement RegisterInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

}
