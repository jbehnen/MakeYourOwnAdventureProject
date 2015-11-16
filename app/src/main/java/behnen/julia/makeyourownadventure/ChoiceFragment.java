package behnen.julia.makeyourownadventure;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import behnen.julia.makeyourownadventure.model.StoryElement;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChoiceFragment extends Fragment {

    private static final String STORY_ELEMENT_ARG = "StoryElement";
    private static final String STORY_TITLE = "StoryTitle";

    private int mChoice1Id;
    private int mChoice2Id;

    private ImageView mImage;

    public static ChoiceFragment newInstance(StoryElement storyElement, String storyTitle) {

        Bundle args = new Bundle();
        args.putSerializable(STORY_ELEMENT_ARG, storyElement);
        args.putString(STORY_TITLE, storyTitle);

        ChoiceFragment fragment = new ChoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_choice, container, false);

        // TODO: test for story element and title existing/not null
        StoryElement choice = (StoryElement) savedInstanceState.getSerializable(STORY_ELEMENT_ARG);
        String title = savedInstanceState.getString(STORY_TITLE);

        displayChoiceElement(v, choice, title);

        // TODO: set button listeners

        return v;
    }

    private final void displayChoiceElement(View v, StoryElement choice, String title) {
        // Load image
        new DownloadImageTask().execute();

        ((TextView) v.findViewById(R.id.choice_element_story_title)).setText(title);
        ((TextView) v.findViewById(R.id.choice_element_element_title)).setText(choice.getTitle());
        ((TextView) v.findViewById(R.id.choice_element_description))
                .setText(choice.getDescription());

        ((Button) v.findViewById(R.id.choice_element_choice1_button))
                .setText(choice.getChoice1Text());
        ((Button) v.findViewById(R.id.choice_element_choice2_button))
                .setText(choice.getChoice2Text());
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
