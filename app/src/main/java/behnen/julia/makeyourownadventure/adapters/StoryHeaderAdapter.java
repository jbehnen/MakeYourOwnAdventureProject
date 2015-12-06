package behnen.julia.makeyourownadventure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import behnen.julia.makeyourownadventure.R;
import behnen.julia.makeyourownadventure.model.StoryHeader;

/**
 * Adapter class for a story header item in a list view.
 *
 * @author Julia
 * @version December 6, 2015
 */
public class StoryHeaderAdapter extends ArrayAdapter<StoryHeader> {

    /**
     * Constructs a new StoryHeaderAdapter.
     * @param context The context for the adapter.
     * @param objects The story header objects to be used in the adapter.
     */
    public StoryHeaderAdapter(Context context, List<StoryHeader> objects) {
        super(context, 0, objects);
    }

    @Override
    public StoryHeader getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StoryHeader storyHeader = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.story_header_item
                    , parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.story_header_item_title);
        TextView author = (TextView) convertView.findViewById(R.id.story_header_item_author);
        TextView storyId = (TextView) convertView.findViewById(R.id.story_header_item_storyId);

        // Populate the data into the template view using the data object
        title.setText(storyHeader.getTitle());
        author.setText(storyHeader.getAuthor());
        storyId.setText(storyHeader.getStoryId());

        // Return the completed view to render on screen
        return convertView;
    }
}
