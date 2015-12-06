package behnen.julia.makeyourownadventure;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
 * A fragment that allows the user to edit a story element.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class EditStoryElementFragment extends Fragment {

    /**
     * The tag used for identifying the story element bundle argument.
     */
    private static final String STORY_ELEMENT = "StoryElement";

    /**
     * The story element being edited.
     */
    private StoryElement mStoryElement;

    /**
     * The list of all story elements in the story.
     */
    private List<StoryElement> mStoryElementList;

    /**
     * The currently selected story element image.
     */
    private ImageView mImage;

    /**
     * The edit texts used for data entry.
     */
    private EditText mTitle;
    private EditText mDescription;
    private EditText mChoice1Text;
    private EditText mChoice2Text;

    // Spinner code derived from
    // http://developer.android.com/guide/topics/ui/controls/spinner.html
    /**
     * The spinners used for data entry.
     */
    private Spinner mImageUrl;
    private Spinner mChoiceEnding;
    private Spinner mChoice1Target;
    private Spinner mChoice2Target;

    /**
     * The context which implements the interface methods.
     */
    OnEditStoryElementInteractionListener mCallback;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnEditStoryElementInteractionListener {
        /**
         * Callback triggered when the user wants to preview the story element.
         * @param storyElement The edited story element.
         */
        void onEditStoryElementPreview(StoryElement storyElement);
        /**
         * Callback triggered when the user wants to save the story element.
         * @param storyElement The edited story element.
         */
        boolean onEditStoryElementSave(StoryElement storyElement);
        /**
         * Callback triggered when the user wants to delete the story element.
         * @param storyElement The story element being deleted.
         */
        boolean onEditStoryElementDelete(StoryElement storyElement);
        /**
         * Callback triggered to get the list of all story elements in the story.
         * @param author The author of the story.
         * @param storyId The storyId of the story.
         * @return The list of all story elements in the story.
         */
        List<StoryElement> onEditStoryElementGetAllElements(String author, String storyId);
    }

    /**
     * DO NOT USE. Only create the fragment using the given newInstance method.
     */
    public EditStoryElementFragment() {

    }

    /**
     * Creates a new instance of EditStoryElementFragment.
     * @param storyElement The story element being edited.
     * @return A new instance of EditStoryElementFragment.
     */
    public static EditStoryElementFragment newInstance(StoryElement storyElement) {
        if (storyElement == null) {
            throw new IllegalArgumentException();
        }
        Bundle args = new Bundle();
        args.putString(STORY_ELEMENT, storyElement.toString());

        EditStoryElementFragment fragment = new EditStoryElementFragment();
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
        getActivity().setTitle(R.string.edit_story_element_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
                R.layout.fragment_edit_story_element, container, false);

        mStoryElement = StoryElement.parseJson(getArguments().getString(STORY_ELEMENT));

        // Image management
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

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnEditStoryElementInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnEditStoryElementInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /**
     * Initializes the spinners.
     * @param view The view being updated.
     */
    private void initializeSpinners(final View view) {
        // Initializing the choice vs ending spinner
        mChoiceEnding = (Spinner) view.findViewById(R.id.edit_story_element_choice_ending_spinner);
        ArrayAdapter<CharSequence> choiceEndingAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.choice_ending_array, android.R.layout.simple_spinner_item);
        choiceEndingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mChoiceEnding.setAdapter(choiceEndingAdapter);
        // Get selection from initial story element
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

        // Initialize the image url spinners
        mImageUrl = (Spinner) view.findViewById(R.id.edit_story_element_image_spinner);
        ArrayAdapter<CharSequence> imageUrlAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.shared_image_names_array, android.R.layout.simple_spinner_item);
        imageUrlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mImageUrl.setAdapter(imageUrlAdapter);
        // Get all possible image URL
        String[] urls = getResources().getStringArray(R.array.shared_image_names_array);
        // Get selection from initial story element
        mImageUrl.setSelection(stringIndex(mStoryElement.getImageUrl(), urls));
        mImageUrl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Updates the displayed image
                new DownloadImageTask().execute((String) mImageUrl.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Initialize the choice target spinners
        mChoice1Target = (Spinner) view.findViewById(R.id.edit_story_element_choice1Id_spinner);
        mChoice2Target = (Spinner) view.findViewById(R.id.edit_story_element_choice2Id_spinner);

        if (mCallback != null) {
            mStoryElementList = mCallback.onEditStoryElementGetAllElements(
                    mStoryElement.getAuthor(), mStoryElement.getStoryId());

            ArrayAdapter<String> mChoice1Adapter =
                    ChoiceTargetAdapter.newInstance(view.getContext(), mStoryElementList);
            ArrayAdapter<String> mChoice2Adapter =
                    ChoiceTargetAdapter.newInstance(view.getContext(), mStoryElementList);
            mChoice1Target.setAdapter(mChoice1Adapter);
            mChoice2Target.setAdapter(mChoice2Adapter);

            // Set selection for targets form initial story element
            mChoice1Target.setSelection(storyElementListPosition(mStoryElement.getChoice1Id()));
            mChoice2Target.setSelection(storyElementListPosition(mStoryElement.getChoice2Id()));
        }
    }

    /**
     * Initializes the buttons.
     * @param view The view being updated.
     */
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
                    if (success) {
                        getFragmentManager().popBackStackImmediate();
                    } else {
                        Toast.makeText(getActivity(), "Save failed", Toast.LENGTH_SHORT).show();
                    }
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
                    // Get the elements that have the current element as the target
                    List<Integer> conflictingElements = externalReferencingStoryElements();
                    if (conflictingElements == null) {
                        Toast.makeText(getActivity(), "Deletion error", Toast.LENGTH_SHORT).show();

                        // If the current element is the start element, don't allow deletion
                    } else if (mStoryElement.getElementId() == StoryElement.START_ID) {
                        Toast.makeText(getActivity(), "Cannot delete start element (element ID: " +
                                        StoryElement.START_ID + ")",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // If no conflicting elements, allow deletion
                        if (conflictingElements.size() == 0) {
                            mCallback.onEditStoryElementDelete(getCurrentStoryElement());
                            Toast.makeText(getActivity(), "Element deleted",
                                    Toast.LENGTH_SHORT).show();
                            getFragmentManager().popBackStackImmediate();
                            // If conflicting elements, notify user and don't delete
                        } else {
                            launchConflictingElementsDialog(conflictingElements);
                        }
                    }
                }
            }
        });
    }

    /**
     * Returns the current, edited version of the story element.
     * @return The current, edited version of the story element.
     */
    private StoryElement getCurrentStoryElement() {
        String title = mTitle.getText().toString();
        String imageUrl = mImageUrl.getSelectedItem().toString();
        String description = mDescription.getText().toString();

        String endingString = mChoiceEnding.getSelectedItem().toString();
        // TODO: ensure consistency using strings.xml
        boolean isEnding = endingString.equals("Ending");

        int choice1Id = mStoryElementList.get(
                mChoice1Target.getSelectedItemPosition()).getElementId();
        int choice2Id = mStoryElementList.get(
                mChoice2Target.getSelectedItemPosition()).getElementId();
        String choice1Text = mChoice1Text.getText().toString();
        String choice2Text = mChoice2Text.getText().toString();

        return  new StoryElement(mStoryElement.getAuthor(), mStoryElement.getStoryId(),
                mStoryElement.getElementId(), title, imageUrl, description, isEnding, choice1Id,
                choice2Id, choice1Text, choice2Text);
    }

    /**
     * Returns all elements in the story that reference this element as a choice
     * target element.
     * @return All elements in the story that reference this element as a choice
     * target element.
     */
    private List<Integer> externalReferencingStoryElements() {
        if (mCallback != null) {
            // Get all story elements
            List<StoryElement> elements = mCallback.onEditStoryElementGetAllElements(
                    mStoryElement.getAuthor(), mStoryElement.getStoryId());
            List<Integer> referencingIds = new ArrayList<>();
            int elementId = mStoryElement.getElementId();
            for (StoryElement element: elements) {
                // If element references current element
                if (elementId == element.getChoice1Id() || elementId == element.getChoice2Id()) {
                    // and element is not current element
                    if (elementId != element.getElementId()) {
                        // This element gets added to the list
                        referencingIds.add(element.getElementId());
                    }
                }
            }
            return referencingIds;
        }
        return null;
    }

    /**
     * Returns the element ID of the story element at the given position in the
     * story element list.
     * @param elementId The id of the story element.
     * @return The element ID of the story element at the given position in the
     * story element list.
     */
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
     * Launches a dialog that shows all of the story elements that have this
     * element as a target.
     *
     * @param elements The conflicting elements: must be of nonzero size.
     */
    private void launchConflictingElementsDialog(List<Integer> elements) {
        StringBuilder sb = new StringBuilder(
                getActivity().getResources().getString(R.string.conflicting_elements));
        sb.append(" ");
        sb.append(elements.get(0));
        for (int i = 1; i < elements.size(); i++) {
            sb.append(", ");
            sb.append(elements.get(i));
        }
        sb.append(".");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(sb.toString());

        dialogBuilder.create().show();
    }

    /**
     * Returns the first index of a string in an array, 0 if no match.
     *
     * @param array The array being searched.
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
