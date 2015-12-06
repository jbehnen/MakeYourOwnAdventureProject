package behnen.julia.makeyourownadventure;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment that displays information about the app.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class AboutFragment extends Fragment {

    /**
     * Resumes fragment and updates activity action bar title.
     *
     * Action bar title update code adapted from
     * http://stackoverflow.com/questions/13472258/
     * handling-actionbar-title-with-the-fragment-back-stack
     */
    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().setTitle(R.string.about_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}
