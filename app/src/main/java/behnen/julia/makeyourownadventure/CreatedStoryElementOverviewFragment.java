package behnen.julia.makeyourownadventure;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import behnen.julia.makeyourownadventure.model.StoryElement;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreatedStoryElementOverviewFragment extends AbstractCreatedStoryElementFragment {

    public static final String STORY_ELEMENT = "StoryElement";

    private OnCreatedStoryElementOverviewInteractionListener mCallback;

    public interface OnCreatedStoryElementOverviewInteractionListener {
        void onCreatedStoryElementOverviewEdit(StoryElement storyElement);
        void onCreatedStoryElementOverviewDelete(StoryElement storyElement);
    }

    public static CreatedStoryElementOverviewFragment newInstance(StoryElement storyElement) {
        Bundle args = new Bundle();
        args.putString(STORY_ELEMENT, storyElement.toString());

        CreatedStoryElementOverviewFragment fragment = new CreatedStoryElementOverviewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Button editButton = (Button) view.findViewById(R.id.created_story_element_button_1);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onCreatedStoryElementOverviewEdit(getStoryElement());
                }
            }
        });

        Button deleteButton = (Button) view.findViewById(R.id.created_story_element_button_2);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onCreatedStoryElementOverviewDelete(getStoryElement());
                }
            }
        });

        Button button3 = (Button) view.findViewById(R.id.created_story_element_button_3);
        button3.setVisibility(View.INVISIBLE);

        return view;
    }



}
