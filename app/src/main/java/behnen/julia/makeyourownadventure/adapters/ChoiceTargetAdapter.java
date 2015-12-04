package behnen.julia.makeyourownadventure.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import behnen.julia.makeyourownadventure.model.StoryElement;

/**
 * Created by Julia on 11/26/2015.
 */
public class ChoiceTargetAdapter extends ArrayAdapter<String> {

    private ChoiceTargetAdapter(Context context, List<String> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
    }

    public static ChoiceTargetAdapter newInstance(Context context, List<StoryElement> objects) {
        List<String> descriptorList = new ArrayList<String>();
        for (StoryElement element: objects) {
            descriptorList.add(element.toTargetDescriptionString());
        }
        ChoiceTargetAdapter adapter = new ChoiceTargetAdapter(context, descriptorList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }
}
