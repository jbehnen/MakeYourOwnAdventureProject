package behnen.julia.makeyourownadventure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import behnen.julia.makeyourownadventure.R;
import behnen.julia.makeyourownadventure.model.StoryElement;

/**
 * Adapter class for a story element item in a list view.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class StoryElementAdapter extends ArrayAdapter<StoryElement> {

    /**
     * Constructs a new StoryElementAdapter.
     * @param context The context for the adapter.
     * @param objects The story element objects to be used in the adapter.
     */
    public StoryElementAdapter(Context context, List<StoryElement> objects) {
        super(context, 0, objects);
    }

    @Override
    public StoryElement getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StoryElement storyElement = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.story_element_item,
                    parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.story_element_item_title);
        TextView elementId =
                (TextView) convertView.findViewById(R.id.story_element_item_element_id);
        TextView choice1Id =
                (TextView) convertView.findViewById(R.id.story_element_item_choice1_id);
        TextView choice2Id =
                (TextView) convertView.findViewById(R.id.story_element_item_choice2_id);

        // Populate the data into the template view using the data object
        title.setText(storyElement.getTitle());
        String elementIdText = getContext().getResources().getString(R.string.label_element_id) +
                " " + Integer.toString(storyElement.getElementId());
        elementId.setText(elementIdText);

        // If it is an ending, hide the layout elements that are used only for choices.
        if (storyElement.isEnding()) {
            choice1Id.setVisibility(View.GONE);
            choice2Id.setVisibility(View.GONE);
        } else {
            // If it is a choice, populate the choice-specific layout elements
            String choice1Text = getContext().getResources().getString(R.string.label_choice1_id) +
                    " " + Integer.toString(storyElement.getChoice1Id());
            String choice2Text = getContext().getResources().getString(R.string.label_choice2_id) +
                    " " + Integer.toString(storyElement.getChoice2Id());
            choice1Id.setText(choice1Text);
            choice2Id.setText(choice2Text);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
