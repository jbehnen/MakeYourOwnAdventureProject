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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.story_element_item
                    , parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.story_element_item_title);
        TextView author = (TextView) convertView.findViewById(R.id.story_element_item_description);
        TextView choiceEnding =
                (TextView) convertView.findViewById(R.id.story_element_item_choice_ending);

        // Populate the data into the template view using the data object
        title.setText(storyElement.getTitle());
        author.setText(storyElement.getAuthor());
        if (storyElement.isEnding()) {
            choiceEnding.setText(R.string.ending);
        } else {
            choiceEnding.setText(R.string.choice);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
