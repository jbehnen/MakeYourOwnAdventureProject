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
 * Created by Julia on 11/26/2015.
 */
public class ChoiceTargetAdapter extends ArrayAdapter<StoryElement> {

    public ChoiceTargetAdapter(Context context, List<StoryElement> objects) {
        super(context, 0, objects);
    }

    @Override
    public StoryElement getItem(int position) {
        return super.getItem(position);
    }

    // Adapted from
    // http://android-er.blogspot.com/2010/12/custom-arrayadapter-for-spinner-with.html
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        StoryElement storyElement = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.target_element_item
                    , parent, false);
        }
        // Lookup view for data population
        TextView text = (TextView) convertView.findViewById(R.id.target_element_item_text);

        // Populate the data into the template view using the data object
        text.setText(storyElement.toTargetDescriptionString());

        // Return the completed view to render on screen
        return convertView;
    }
}
