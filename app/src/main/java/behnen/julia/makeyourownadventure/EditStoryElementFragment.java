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
public class EditStoryElementFragment extends AbstractCreatedStoryElementFragment {

    public interface OnEditStoryElementInteractionListener {
        void onEditStoryElementPreview(StoryElement storyElement);
        void onEditStoryElementSave(StoryElement storyElement);
        void onEditStoryElementCancel();
    }

    public static EditStoryElementFragment newInstance(StoryElement storyElement) {
        Bundle args = new Bundle();
        args.putString(STORY_ELEMENT, storyElement.toString());

        EditStoryElementFragment fragment = new EditStoryElementFragment();
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
