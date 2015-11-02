package behnen.julia.makeyourownadventure;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import behnen.julia.makeyourownadventure.data.StoryDB;
import behnen.julia.makeyourownadventure.model.Story;
import behnen.julia.makeyourownadventure.support.AbstractPostAsyncTask;
import behnen.julia.makeyourownadventure.support.AbstractStoryCheckTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDownloadedStoriesListInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DownloadedStoriesListFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class DownloadedStoriesListFragment extends android.support.v4.app.Fragment
        implements AbsListView.OnItemClickListener {

    private OnDownloadedStoriesListInteractionListener mCallback;

    private ArrayList<Story> mStoryList;
    private ProgressDialog mProgressDialog;
    private String mStories;
    private TextView mStoryErrorText;
    private StoryDB storyDb;
    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mStoriesListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_downloaded_stories_list, container, false);

        mStoryErrorText = (TextView) view.findViewById(R.id.story_error);

        mStoriesListView = (ListView) view.findViewById(R.id.story_list_view);
        mStoriesListView.setOnItemClickListener(this);

        mStoryList = new ArrayList<>();
        mAdapter = new StoryAdapter(view.getContext(), mStoryList);

        storyDb = new StoryDB(getContext());
        loadDatabaseStories();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnDownloadedStoriesListInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDownloadedStoriesListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mCallback) {
            mCallback.onStorySelected(mStoryList.get(position));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDownloadedStoriesListInteractionListener {
        // TODO: Update argument type and name
        public void onStorySelected(Story story);
    }

    private void loadDatabaseStories() {
        ArrayList<Story> databaseStories = storyDb.selectAll();
        for (Story story: databaseStories) {
            mStoryList.add(story);
        }
        mStoriesListView.setAdapter(mAdapter);
    }
}
