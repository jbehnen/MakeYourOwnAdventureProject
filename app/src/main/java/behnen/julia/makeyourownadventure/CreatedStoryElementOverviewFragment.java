package behnen.julia.makeyourownadventure;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import behnen.julia.makeyourownadventure.model.StoryElement;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreatedStoryElementOverviewFragment extends AbstractCreatedStoryElementFragment {

    public static final String STORY_ELEMENT = "StoryElement";


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


        return view;
    }

}
