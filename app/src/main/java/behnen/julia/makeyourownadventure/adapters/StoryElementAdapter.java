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
 * Created by Julia on 11/25/2015.
 */
public class StoryElementAdapter extends ArrayAdapter<StoryElement> {

    private static final String ELEMENT_ID_PREFIX = "ID: ";
    private static final String CHOICE1_ID_PREFIX = "Choice 1: ";
    private static final String CHOICE2_ID_PREFIX = "Choice 2: ";

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
        elementId.setText(ELEMENT_ID_PREFIX + Integer.toString(storyElement.getElementId()));
        if (storyElement.isEnding()) {
            choice1Id.setVisibility(View.GONE);
            choice2Id.setVisibility(View.GONE);
        } else {
            choice1Id.setText(CHOICE1_ID_PREFIX + Integer.toString(storyElement.getChoice1Id()));
            choice2Id.setText(CHOICE2_ID_PREFIX + Integer.toString(storyElement.getChoice2Id()));
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
