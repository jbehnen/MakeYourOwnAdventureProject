package behnen.julia.makeyourownadventure.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import behnen.julia.makeyourownadventure.model.StoryElement;

/**
 * Adapter class for a spinner that displays a list of story elements.
 * It stores and displays strings instead of story elements, in the same
 * order as the story elements that are used to construct it.
 *
 * @author Julia
 * @version December 6, 2015
 */
public class ChoiceTargetAdapter extends ArrayAdapter<String> {

    /**
     * Private constructor for ChoiceTargetAdapter.
     * @param context The context of the adapter
     * @param objects The objects put into the adapter
     */
    private ChoiceTargetAdapter(Context context, List<String> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
    }

    /**
     * Creates a new instance of ChoiceTargetAdapter that contains string representations
     * of the story elements' IDs and titles.
     * @param context The context of the adapter
     * @param objects The objects put into the adapter
     * @return A new instance of ChoiceTargetAdapter that contains string representations
     * of the story elements' IDs and titles.
     */
    public static ChoiceTargetAdapter newInstance(Context context, List<StoryElement> objects) {
        List<String> descriptorList = new ArrayList<>();
        for (StoryElement element: objects) {
            descriptorList.add(element.toTargetDescriptionString());
        }
        ChoiceTargetAdapter adapter = new ChoiceTargetAdapter(context, descriptorList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }
}
