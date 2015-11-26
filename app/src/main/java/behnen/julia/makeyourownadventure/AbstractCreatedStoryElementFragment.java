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
public class AbstractCreatedStoryElementFragment extends Fragment {

    protected static final String STORY_ELEMENT = "StoryElement";

    private StoryElement mOriginalStoryElement;

    private Bitmap mImage;

    private EditText mTitle;
    private EditText mDescription;
    private EditText mChoice1Text;
    private EditText mChoice2Text;

    private Spinner mImageUrl;
    private Spinner mChoiceEnding;
    private Spinner mChoice1Target;
    private Spinner mChoice2Target;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
                R.layout.fragment_created_story_element, container, false);

        // TODO check valid arguments
        mOriginalStoryElement = StoryElement.parseJson(getArguments().getString(STORY_ELEMENT));

        mTitle = (EditText) view.findViewById(R.id.created_story_element_title);
        mDescription = (EditText) view.findViewById(R.id.created_story_element_description);
        mChoice1Text = (EditText) view.findViewById(R.id.created_story_element_choice1Text);
        mChoice2Text = (EditText) view.findViewById(R.id.created_story_element_choice2Text);

        mTitle.setText(mOriginalStoryElement.getTitle());
        mDescription.setText(mOriginalStoryElement.getDescription());
        mChoice1Text.setText(mOriginalStoryElement.getChoice1Text());
        mChoice2Text.setText(mOriginalStoryElement.getChoice2Text());

        return view;
    }

    protected StoryElement getStoryElement() {
        // todo: replace with actual value
        return mOriginalStoryElement;
    }

}
