package behnen.julia.makeyourownadventure;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import behnen.julia.makeyourownadventure.data.StoryDB;
import behnen.julia.makeyourownadventure.model.Story;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDownloadedStoriesListInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DownloadedStoriesListFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class DownloadedStoriesListFragment extends Fragment
        implements AbsListView.OnItemClickListener {

    private OnDownloadedStoriesListInteractionListener mCallback;

    private ArrayList<Story> mStoryList;
    private ProgressDialog mProgressDialog;
    private String mStories;
    private TextView mStoryErrorText;
    private StoryDB storyDb;

    // Placeholders for demonstration purposes
    private static final String DEMO_STORY = "demo_story";
    private Story mDemoStory;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mStoriesListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // placeholder code for demo

    public static DownloadedStoriesListFragment newInstance(String serializedStory) {
        Bundle args = new Bundle();
        args.putSerializable(DEMO_STORY, serializedStory);

        DownloadedStoriesListFragment fragment = new DownloadedStoriesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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

        // placeholder code for demo
        if (getArguments() != null) {
            String serializedStory = (String) getArguments().getSerializable(DEMO_STORY);
            mStoryList.add(new Story(serializedStory));
        }
        // end placeholder

        loadDatabaseStories();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_download_stories_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_download_new_story:
                if(mCallback != null) {
                    mCallback.onDownloadedStoriesListDownloadNewStory();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void onResume() {
        super.onResume();
        loadDatabaseStories();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mCallback) {
            mCallback.onDownloadedStoriesListStorySelected(mStoryList.get(position));
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
        public void onDownloadedStoriesListStorySelected(Story story);
        public void onDownloadedStoriesListDownloadNewStory();
    }

    private void loadDatabaseStories() {
        ArrayList<Story> databaseStories = storyDb.selectAll();
        for (Story story: databaseStories) {
            mStoryList.add(story);
        }
        mStoriesListView.setAdapter(mAdapter);
    }
}
