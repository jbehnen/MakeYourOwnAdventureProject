package behnen.julia.makeyourownadventure;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import behnen.julia.makeyourownadventure.adapters.ChoiceTargetAdapter;
import behnen.julia.makeyourownadventure.model.StoryElement;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditStoryElementFragment extends Fragment {

    private static final String STORY_ELEMENT = "StoryElement";

    private static final String TAG = "EditStoryElement";

    private StoryElement mStoryElement;

    private List<StoryElement> mStoryElementList;

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
        boolean onEditStoryElementSave(StoryElement storyElement);
        boolean onEditStoryElementDelete(StoryElement storyElement);
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
        Log.d(TAG, "incoming element: " + mStoryElement.toString());

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
        mChoiceEnding = (Spinner) view.findViewById(R.id.edit_story_element_choice_ending_spinner);
        ArrayAdapter<CharSequence> choiceEndingAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.choice_ending_array, android.R.layout.simple_spinner_item);
        choiceEndingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mChoiceEnding.setAdapter(choiceEndingAdapter);

        mChoice1Target = (Spinner) view.findViewById(R.id.edit_story_element_choice1Id_spinner);
        mChoice2Target = (Spinner) view.findViewById(R.id.edit_story_element_choice2Id_spinner);

        mImageUrl = (Spinner) view.findViewById(R.id.edit_story_element_image_spinner);
        ArrayAdapter<CharSequence> imageUrlAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.shared_image_names_array, android.R.layout.simple_spinner_item);
        imageUrlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mImageUrl.setAdapter(imageUrlAdapter);

        if (mCallback != null) {
            mStoryElementList = mCallback.onEditStoryElementGetAllElements(
                    mStoryElement.getAuthor(), mStoryElement.getStoryId());
            List<String> descriptorList = new ArrayList<String>();
            for (StoryElement element: mStoryElementList) {
                descriptorList.add(element.toTargetDescriptionString());
            }

            ArrayAdapter<String> adapter =
                    ChoiceTargetAdapter.newInstance(view.getContext(), mStoryElementList);
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
                    boolean success = mCallback.onEditStoryElementSave(getCurrentStoryElement());
                    getFragmentManager().popBackStackImmediate();
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
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    List<Integer> conflictingElements = externalReferencingStoryElements();
                    if (conflictingElements == null) {
                        Toast.makeText(getActivity(), "Deletion error", Toast.LENGTH_SHORT).show();
                    } else if (mStoryElement.getElementId() == StoryElement.START_ID) {
                        Toast.makeText(getActivity(), "Cannot delete start element (element ID: " +
                                        StoryElement.START_ID + ")",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (conflictingElements.size() == 0) {
                            mCallback.onEditStoryElementDelete(getCurrentStoryElement());
                            Toast.makeText(getActivity(), "Element deleted",
                                    Toast.LENGTH_SHORT).show();
                            getFragmentManager().popBackStackImmediate();
                        } else {
                            String badElements = "";
                            for (int i : conflictingElements) {
                                badElements += " " + i;
                            }
                            Toast.makeText(getActivity(), "Element cannot be deleted while it" +
                                            "is referenced in elements" + badElements,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
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
        String title = mTitle.getText().toString();
        String imageUrl = mImageUrl.getSelectedItem().toString();
        String description = mTitle.getText().toString();

        String endingString = mChoiceEnding.getSelectedItem().toString();
        // TODO: ensure consistency using strings.xml
        Log.d(TAG, "Ending string: " + endingString);
        boolean isEnding = endingString.equals("Ending");
        Log.d(TAG, "isEnding: " + isEnding);

        // TODO: implement IDs
        int choice1Id = 0;
        int choice2Id = 0;
        String choice1Text = mChoice1Text.getText().toString();
        String choice2Text = mChoice2Text.getText().toString();

        StoryElement storyElement = new StoryElement(mStoryElement.getAuthor(), mStoryElement.getStoryId(),
                mStoryElement.getElementId(), title, imageUrl, description, isEnding, choice1Id,
                choice2Id, choice1Text, choice2Text);
        Log.d(TAG, storyElement.toString());
        return storyElement;
    }

    private List<Integer> externalReferencingStoryElements() {
        if (mCallback != null) {
            List<StoryElement> elements = mCallback.onEditStoryElementGetAllElements(
                    mStoryElement.getAuthor(), mStoryElement.getStoryId());
            List<Integer> referencingIds = new ArrayList<>();
            int elementId = mStoryElement.getElementId();
            for (StoryElement element: elements) {
                if (elementId == element.getChoice1Id() || elementId == element.getChoice2Id()) {
                    if (elementId != element.getElementId()) {
                        referencingIds.add(element.getElementId());
                    }
                }
            }
            return referencingIds;
        }
        return null;
    }

}
