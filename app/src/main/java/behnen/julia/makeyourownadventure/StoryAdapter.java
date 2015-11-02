package behnen.julia.makeyourownadventure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import behnen.julia.makeyourownadventure.model.Story;

/**
 * Created by Julia on 11/1/2015.
 */
public class StoryAdapter extends ArrayAdapter<Story> {

    public StoryAdapter(Context context, ArrayList<Story> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Story story = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_story_item, parent, false);
        }

        // Look up view for data population
        TextView title = (TextView) convertView.findViewById(R.id.item_story_title);
        TextView description = (TextView) convertView.findViewById(R.id.item_story_description);
        TextView author = (TextView) convertView.findViewById(R.id.item_story_author);
        TextView id = (TextView) convertView.findViewById(R.id.item_story_id);

        title.setText(story.getTitle());
        description.setText(story.getDescription());
        author.setText(story.getAuthor());
        id.setText(story.getStoryId());

        return convertView;
    }

}
