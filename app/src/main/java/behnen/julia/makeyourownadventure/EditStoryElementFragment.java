package behnen.julia.makeyourownadventure;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import behnen.julia.makeyourownadventure.model.StoryElement;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditStoryElementFragment extends Fragment {

    private static final String STORY_ELEMENT = "StoryElement";

    private StoryElement mStoryElement;

    private Bitmap mImage;

    private EditText mTitle;
    private EditText mDescription;
    private EditText mChoice1Text;
    private EditText mChoice2Text;

    private Spinner mImageUrl;
    private Spinner mChoiceEnding;
    private Spinner mChoice1Target;
    private Spinner mChoice2Target;

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
        View view = inflater.inflate(
                R.layout.fragment_edit_story_element, container, false);

        // TODO check valid arguments
        mStoryElement = StoryElement.parseJson(getArguments().getString(STORY_ELEMENT));

        mTitle = (EditText) view.findViewById(R.id.edit_storyViewById(R.id.edit_story_element_title);
        mDescription = (EditText) view.findViewById(R.id.edit_story_element_description);
        mChoice1Text = (EditText) view.findViewById(R.id.edit_story_element_choice1Text);
        mChoice2Text = (EditText) view.findViewById(R.id.edit_story_element_choice2Text);

        mTitle.setText(mStoryElement.getTitle());
        mDescription.setText(mStoryElement.getDescription());
        mChoice1Text.setText(mStoryElement.getChoice1Text());
        mChoice2Text.setText(mStoryElement.getChoice2Text());utton) view.findViewById(R.id.edit_story_element_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

}
