package behnen.julia.makeyourownadventure;

import android.app.Activity;
import android.content.Context;
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

    private MainMenuInteractionListener mCallback;

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

        mContinueStoryButton = (Button) v.findViewById(R.id.main_menu_continue_story_button);
        mDownloadedStoriesButton = (Button) v.findViewById(R.id.main_menu_bookmarked_stories_button);
        mMyStoriesButton = (Button) v.findViewById(R.id.main_menu_create_edit_stories_button);
        mAboutButton = (Button) v.findViewById(R.id.main_menu_about_button);
        mSignOutButton = (Button) v.findViewById(R.id.main_menu_sign_out_button);

        mDownloadedStoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuDownloadedStoriesAction();
                }
            }
        });

        mMyStoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuMyStoriesAction();
                }
            }
        });

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuSignOutAction();
                }
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (MainMenuInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement MainMenuInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface MainMenuInteractionListener {
        void onMainMenuContinueStoryAction();
        void onMainMenuDownloadedStoriesAction();
        void onMainMenuMyStoriesAction();
        void onMainMenuAboutAction();
        void onMainMenuSignOutAction();
    }

}
