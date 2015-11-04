package behnen.julia.makeyourownadventure;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A fragment that provides the central navigation of the app in the form of a menu.
 *
 * @author Julia Behnen
 * @version November 4, 2015
 */
public class MainMenuFragment extends Fragment {

    /**
     * The context which implements the interface methods.
     */
    private MainMenuInteractionListener mCallback;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface MainMenuInteractionListener {

        /**
         * Callback triggered when the user has pressed the "Continue Story" button
         * in MainMenuFragment.
         */
        void onMainMenuContinueStoryAction();

        /**
         * Callback triggered when the user has pressed the "The Story So Far" button
         * in MainMenuFragment.
         */
        void onMainMenuStorySoFarAction();

        /**
         * Callback triggered when the user has pressed the "Bookmarked Stories" button
         * in MainMenuFragment.
         */
        void onMainMenuBookmarkedStoriesAction();

        /**
         * Callback triggered when the user has pressed the "Create or Edit Stories" button
         * in MainMenuFragment.
         */
        void onMainMenuCreateEditStoriesAction();

        /**
         * Callback triggered when the user has pressed the "About" button
         * in MainMenuFragment.
         */
        void onMainMenuAboutAction();

        /**
         * Callback triggered when the user has pressed the "Sign Out" button
         * in MainMenuFragment.
         */
        void onMainMenuSignOutAction();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        Button continueStoryButton = (Button) v.findViewById(R.id.main_menu_continue_story_button);
        Button storySoFarButton = (Button) v.findViewById(R.id.main_menu_the_story_so_far_button);
        Button downloadedStoriesButton = (Button) v.findViewById(R.id.main_menu_bookmarked_stories_button);
        Button myStoriesButton = (Button) v.findViewById(R.id.main_menu_create_edit_stories_button);
        Button aboutButton = (Button) v.findViewById(R.id.main_menu_about_button);
        Button signOutButton = (Button) v.findViewById(R.id.main_menu_sign_out_button);

        continueStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuContinueStoryAction();
                }
            }
        });

        storySoFarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuStorySoFarAction();
                }
            }
        });

        downloadedStoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuBookmarkedStoriesAction();
                }
            }
        });

        myStoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuCreateEditStoriesAction();
                }
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMainMenuAboutAction();
                }
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
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

}
