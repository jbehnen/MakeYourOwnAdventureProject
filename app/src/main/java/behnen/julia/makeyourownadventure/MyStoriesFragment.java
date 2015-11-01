package behnen.julia.makeyourownadventure;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Julia on 11/1/2015.
 */
public class MyStoriesFragment extends Fragment {

    private MyStoriesInteractionListener mCallback;
    private Button mDefaultStoryButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_stories, container, false);

        mDefaultStoryButton = (Button) v.findViewById(R.id.default_story_upload_button);

        mDefaultStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptStoryUpload();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (MyStoriesInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement MyStoriesInteractionListener");
        }
    }

    public interface MyStoriesInteractionListener {
    }

    private void attemptStoryUpload() {

    }
}
