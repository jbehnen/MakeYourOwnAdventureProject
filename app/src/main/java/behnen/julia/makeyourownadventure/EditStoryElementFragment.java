package behnen.julia.makeyourownadventure;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import behnen.julia.makeyourownadventure.adapters.ChoiceTargetAdapter;
import behnen.julia.makeyourownadventure.model.StoryElement;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditStoryElementFragment extends Fragment {

    protected static final String STORY_ELEMENT = "StoryElement";

    private StoryElement mStoryElement;

    private Bitmap mImage;

    private EditText mTitle;
    private EditText mDescription;
    private EditText mChoice1Text;
    private EditText mChoice2Text;

    // Spinner code derived from
    // http://developer.android.com/guide/topics/ui/controls/spinner.html
    private Spinner mImageUrl;
    private Spinner mChoiceEnding;
    private Spinner mChoice1Target;
    private Spinner mChoice2Target;

    OnEditStoryElementInteractionListener mCallback;

    public interface OnEditStoryElementInteractionListener {
        void onEditStoryElementPreview(StoryElement storyElement);
        void onEditStoryElementSave(StoryElement storyElement);
        void onEditStoryElementDelete(StoryElement storyElement);
        List<StoryElement> onEditStoryElementGetAllElements(String author, String storyId);
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
        View view = inflater.inflate(
                R.layout.fragment_edit_story_element, container, false);

        // TODO check valid arguments
        mStoryElement = StoryElement.parseJson(getArguments().getString(STORY_ELEMENT));

        // EditText management
        mTitle = (EditText) view.findViewById(R.id.edit_story_element_title);
        mDescription = (EditText) view.findViewById(R.id.edit_story_element_description);
        mChoice1Text = (EditText) view.findViewById(R.id.edit_story_element_choice1Text);
        mChoice2Text = (EditText) view.findViewById(R.id.edit_story_element_choice2Text);

        mTitle.setText(mStoryElement.getTitle());
        mDescription.setText(mStoryElement.getDescription());
        mChoice1Text.setText(mStoryElement.getChoice1Text());
        mChoice2Text.setText(mStoryElement.getChoice2Text());

        // Spinner management
        mChoice1Target = (Spinner) view.findViewById(R.id.edit_story_element_choice1Id_spinner);
        mChoice2Target = (Spinner) view.findViewById(R.id.edit_story_element_choice2Id_spinner);

        mChoiceEnding = (Spinner) view.findViewById(R.id.edit_story_element_choice_ending_spinner);
        ArrayAdapter<CharSequence> choiceEndingAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.choice_ending_array, android.R.layout.simple_spinner_item);
        choiceEndingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mChoiceEnding.setAdapter(choiceEndingAdapter);

        if (mCallback != null) {
            List<StoryElement> list = mCallback.onEditStoryElementGetAllElements(
                    mStoryElement.getAuthor(), mStoryElement.getStoryId());
            List<String> descriptorList = new ArrayList<String>();
            for (StoryElement element: list) {
                descriptorList.add(element.toTargetDescriptionString());
            }

//            ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                    getActivity(), R.layout.support_simple_spinner_dropdown_item, descriptorList);
            ArrayAdapter<StoryElement> adapter = new ChoiceTargetAdapter(view.getContext(), list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mChoice1Target.setAdapter(adapter);
            mChoice2Target.setAdapter(adapter);
        }

        Button previewButton = (Button) view.findViewById(R.id.edit_story_element_preview_button);
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onEditStoryElementPreview(getCurrentStoryElement());
                }
            }
        });

        Button saveButton = (Button) view.findViewById(R.id.edit_story_element_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onEditStoryElementSave(getCurrentStoryElement());
                }
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.edit_story_element_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        Button deleteButton = (Button) view.findViewById(R.id.edit_story_element_delete_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onEditStoryElementDelete(getCurrentStoryElement());
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnEditStoryElementInteractionListener) context;
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

    public StoryElement getCurrentStoryElement() {
        // TODO: fill in later
        return mStoryElement;
    }

}
