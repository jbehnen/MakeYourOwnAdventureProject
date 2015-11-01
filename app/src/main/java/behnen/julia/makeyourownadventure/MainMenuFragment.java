package behnen.julia.makeyourownadventure;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Julia on 10/31/2015.
 */
public class MainMenuFragment extends Fragment {

    private OnMainMenuInteractionListener mCallback;

    private Button mContinueStoryButton;
    private Button mDownloadedStoriesButton;
    private Button mMyStoriesButton;
    private Button mAboutButton;
    private Button mSignOutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        mContinueStoryButton = (Button) v.findViewById(R.id.continue_story);
        mDownloadedStoriesButton = (Button) v.findViewById(R.id.downloaded_stories);
        mMyStoriesButton = (Button) v.findViewById(R.id.my_stories);
        mAboutButton = (Button) v.findViewById(R.id.about);
        mSignOutButton = (Button) v.findViewById(R.id.sign_out);

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMainMenuSignOutAction();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnMainMenuInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement OnSignInInteractionListener");
        }
    }

    public interface OnMainMenuInteractionListener {
        public void onMainMenuContinueStoryAction();
        public void onMainMenuDownloadedStoriesAction();
        public void onMainMenuMyStoriesAction();
        public void onMainMenuAboutAction();
        public void onMainMenuSignOutAction();
    }

}
