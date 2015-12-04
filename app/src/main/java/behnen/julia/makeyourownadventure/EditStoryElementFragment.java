package behnen.julia.makeyourownadventure;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import behnen.julia.makeyourownadventure.adapters.ChoiceTargetAdapter;
import behnen.julia.makeyourownadventure.asyncs.AbstractDownloadImageTask;
import behnen.julia.makeyourownadventure.model.StoryElement;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditStoryElementFragment extends Fragment {

    private static final String STORY_ELEMENT = "StoryElement";

    private static final String TAG = "EditStoryElement";

    private StoryElement mStoryElement;

    private List<StoryElement> mStoryElementList;

    private ImageView mImage;

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

        // Image management
        // TODO: handle image load failure
        mImage = (ImageView) view.findViewById(R.id.edit_story_element_image);
        new DownloadImageTask().execute(mStoryElement.getImageUrl());

        // EditText management
        mTitle = (EditText) view.findViewById(R.id.edit_story_element_title);
        mDescription = (EditText) view.findViewById(R.id.edit_story_element_description);
        mChoice1Text = (EditText) view.findViewById(R.id.edit_story_element_choice1Text);
        mChoice2Text = (EditText) view.findViewById(R.id.edit_story_element_choice2Text);

        mTitle.setText(mStoryElement.getTitle());
        mDescription.setText(mStoryElement.getDescription());
        mChoice1Text.setText(mStoryElement.getChoice1Text());
        mChoice2Text.setText(mStoryElement.getChoice2Text());

        initializeSpinners(view);
        initializeButtons(view);

        // TODO: toggle based on choice/not choice

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

    private void initializeSpinners(final View view) {
        mChoiceEnding = (Spinner) view.findViewById(R.id.edit_story_element_choice_ending_spinner);
        ArrayAdapter<CharSequence> choiceEndingAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.choice_ending_array, android.R.layout.simple_spinner_item);
        choiceEndingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mChoiceEnding.setAdapter(choiceEndingAdapter);
        int selection = mStoryElement.isEnding() ? 1 : 0;
        mChoiceEnding.setSelection(selection);
        mChoiceEnding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View theView, int position, long id) {
                LinearLayout choiceElements = (LinearLayout)
                        view.findViewById(R.id.edit_story_element_choice_elements);
                if (position == 0) { // choice
                    choiceElements.setVisibility(View.VISIBLE);
                } else {  // ending
                    choiceElements.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mChoice1Target = (Spinner) view.findViewById(R.id.edit_story_element_choice1Id_spinner);
        mChoice2Target = (Spinner) view.findViewById(R.id.edit_story_element_choice2Id_spinner);

        mImageUrl = (Spinner) view.findViewById(R.id.edit_story_element_image_spinner);
        ArrayAdapter<CharSequence> imageUrlAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.shared_image_names_array, android.R.layout.simple_spinner_item);
        imageUrlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mImageUrl.setAdapter(imageUrlAdapter);
        String[] urls = getResources().getStringArray(R.array.shared_image_names_array);
        mImageUrl.setSelection(stringIndex(mStoryElement.getImageUrl(), urls));
        mImageUrl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new DownloadImageTask().execute((String) mImageUrl.getSelectedItem());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if (mCallback != null) {
            mStoryElementList = mCallback.onEditStoryElementGetAllElements(
                    mStoryElement.getAuthor(), mStoryElement.getStoryId());

            ArrayAdapter<String> mChoice1Adapter =
                    ChoiceTargetAdapter.newInstance(view.getContext(), mStoryElementList);
            ArrayAdapter<String> mChoice2Adapter =
                    ChoiceTargetAdapter.newInstance(view.getContext(), mStoryElementList);
            mChoice1Target.setAdapter(mChoice1Adapter);
            mChoice2Target.setAdapter(mChoice2Adapter);

            mChoice1Target.setSelection(storyElementListPosition(mStoryElement.getChoice1Id()));
            mChoice2Target.setSelection(storyElementListPosition(mStoryElement.getChoice2Id()));
        }
    }

    private void initializeButtons(View view) {
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
                            launchConflictingElementsDialog(conflictingElements);
                        }
                    }
                }
            }
        });
    }

    private StoryElement getCurrentStoryElement() {
        String title = mTitle.getText().toString();
        String imageUrl = mImageUrl.getSelectedItem().toString();
        String description = mDescription.getText().toString();

        String endingString = mChoiceEnding.getSelectedItem().toString();
        // TODO: ensure consistency using strings.xml
        Log.d(TAG, "Ending string: " + endingString);
        boolean isEnding = endingString.equals("Ending");
        Log.d(TAG, "isEnding: " + isEnding);

        // TODO: implement IDs
        int choice1Id = mStoryElementList.get(
                mChoice1Target.getSelectedItemPosition()).getElementId();
        int choice2Id = mStoryElementList.get(
                mChoice2Target.getSelectedItemPosition()).getElementId();;
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

    private int storyElementListPosition(int elementId) {
        int index = 0;
        for (int i = 0; i < mStoryElementList.size(); i++) {
            if (mStoryElementList.get(i).getElementId() == elementId) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     *
     *
     * @param elements Must be of nonzero size.
     */
    private void launchConflictingElementsDialog(List<Integer> elements) {
        StringBuilder sb = new StringBuilder("Element cannot be deleted while it " +
                "is referenced in element(s) " );
        int listSize = elements.size();
        sb.append(elements.get(0));
        for (int i = 1; i < listSize - 1; i++) {
            sb.append(", " + elements.get(i));
        }
        if (listSize == 2) {
            sb.append(" and " + elements.get(listSize - 1));
        }
        if (listSize > 2) {
            sb.append(", and " + elements.get(listSize - 1));
        }
        sb.append(".");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(sb.toString());

        dialogBuilder.create().show();
    }

    /**
     * Returns the first index of a string in an array, 0 if no match.
     *
     * @param array
     * @return the first index of a string in an array, 0 if no match.
     */
    private int stringIndex(String string, String[] array) {
        int index = 0;

        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(string)) {
                index = i;
                break;
            }
        }

        return index;
    }

    /**
     * An asynchronous task for downloading a story element image.
     */
    private class DownloadImageTask extends AbstractDownloadImageTask {
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImage.setImageBitmap(bitmap);
        }
    }
}
