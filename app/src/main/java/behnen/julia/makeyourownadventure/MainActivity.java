package behnen.julia.makeyourownadventure;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import behnen.julia.makeyourownadventure.data.BookmarkedStoryDB;
import behnen.julia.makeyourownadventure.model.StoryElement;
import behnen.julia.makeyourownadventure.model.StoryHeader;

/**
 * The main activity for MakeYourOwnAdventure.
 *
 * This is the only activity for the app, it contains and manages all of the fragments
 * that build the app.
 *
 * @author Julia Behnen
 * @version November 4, 2015
 */
public class MainActivity extends AppCompatActivity implements
        SignInFragment.OnSignInInteractionListener,
        MainMenuFragment.MainMenuInteractionListener,
        BookmarkedStoriesFragment.OnBookmarkedStoriesInteractionListener,
        GetNewStoryFragment.OnGetNewStoryInteractionListener,
        StoryOverviewFragment.OnStoryOverviewInteractionListener,
        StoryElementFragment.OnStoryElementInteractionListener {

    /**
     * The URL for story element download requests.
     */
    private static final String GET_STORY_ELEMENT_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/getStoryElement.php";
    private static final String TAG = "MainActivity";

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), MODE_PRIVATE);
        boolean loggedIn = mSharedPreferences.getBoolean(
                getString(R.string.LOGGEDIN), false);
        if (findViewById(R.id.main_fragment_container) != null) {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            if (!loggedIn) {
                Fragment loginFragment = new SignInFragment();
                fragmentTransaction.add(R.id.main_fragment_container, loginFragment)
                        .commit();
            }
            else {
                Fragment menuFragment = new MainMenuFragment();
                fragmentTransaction.add(R.id.main_fragment_container, menuFragment)
                        .commit();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Shared methods

    private String getCurrentUser() {
        return mSharedPreferences.getString(getString(R.string.USERNAME), null);
    }

    private void launchStoryElement(StoryElement storyElement, boolean eraseLast) {
        // TODO: check for local vs online
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_container,
                        StoryElementFragment.newInstance(storyElement));
        if (eraseLast) {
            getSupportFragmentManager().popBackStack();
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    // Bookmarked Stories database methods

    private boolean addBookmarkedStory(StoryHeader storyHeader) {
        BookmarkedStoryDB bookmarkedStoryDB = new BookmarkedStoryDB(this);
        String username = getCurrentUser();
        boolean wasAdded = bookmarkedStoryDB.insertStory(username, storyHeader);
        bookmarkedStoryDB.closeDB();
        return wasAdded;
    }

    private boolean deleteBookmarkedStory(String author, String storyId) {
        BookmarkedStoryDB bookmarkedStoryDB = new BookmarkedStoryDB(this);
        String username = getCurrentUser();
        boolean wasDeleted = bookmarkedStoryDB.deleteStory(username, author, storyId);
        bookmarkedStoryDB.closeDB();
        return wasDeleted;
    }

    private List<StoryHeader> getBookmarkedStories() {
        BookmarkedStoryDB bookmarkedStoryDB = new BookmarkedStoryDB(this);
        String username = getCurrentUser();
        List<StoryHeader> list = bookmarkedStoryDB.getStoriesByUsername(username);
        bookmarkedStoryDB.closeDB();
        return list;
    }

    // SignInFragment callback methods

    @Override
    public void onSignInSignInAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new MainMenuFragment())
                .commit();
    }

    @Override
    public void onSignInRegisterAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }

    // MainMenuFragment callback methods

    @Override
    public void onMainMenuContinueStoryAction() {
        // todo make sure proper backstack transaction name
    }

    @Override
    public void onMainMenuStorySoFarAction() {

    }

    @Override
    public void onMainMenuBookmarkedStoriesAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new BookmarkedStoriesFragment())
                .addToBackStack(getClass().getName())
                .commit();
    }

    @Override
    public void onMainMenuCreateEditStoriesAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new CreateEditStoriesFragment())
                .addToBackStack(getClass().getName())
                .commit();
    }

    @Override
    public void onMainMenuAboutAction() {

    }

    @Override
    public void onMainMenuSignOutAction() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(getString(R.string.USERNAME), null);
        editor.putBoolean(getString(R.string.LOGGEDIN), false);
        editor.commit();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new SignInFragment())
                .commit();
    }

    // BookmarkedStoriesFragment callback methods

    @Override
    public List<StoryHeader> onBookmarkedStoriesGetStories() {
        return getBookmarkedStories();
    }

    @Override
    public void onBookmarkedStoriesSelectStory(StoryHeader storyHeader) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container,
                        StoryOverviewFragment.newInstance(storyHeader))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBookmarkedStoriesAddStory() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new GetNewStoryFragment())
                .addToBackStack(null)
                .commit();
    }

    // GetNewStoryFragment callback methods

    @Override
    public boolean onGetNewStoryAddStory(StoryHeader storyHeader) {
        return addBookmarkedStory(storyHeader);
    }

    // StoryOverviewFragment callback methods

    @Override
    public void onStoryOverviewFragmentPlayStory(String author, String storyId) {
        new StoryGetElementTask(false)
                .execute(author, storyId, Integer.toString(StoryElement.START_ID));
    }

    @Override
    public boolean onStoryOverviewFragmentDeleteStory(String author, String storyId) {
        return deleteBookmarkedStory(author, storyId);
    }

    // StoryElementFragment callback methods

    @Override
    public void onStoryElementChoiceAction(String author, String storyId, int elementId) {
        new StoryGetElementTask(true)
                .execute(author, storyId, Integer.toString(elementId));
    }

    @Override
    public void onStoryElementBacktrackAction() {
        // TODO: replace
        Toast.makeText(this, "Will eventually backtrack", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStoryElementMainMenuAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new MainMenuFragment())
                .commit();    }

    // Shared Async Methods
    /**
     * Downloads a StoryElement from the online database.
     */
    public class StoryGetElementTask extends AbstractPostAsyncTask<String, Void, String> {

        private boolean mAddToBackstack;

        public StoryGetElementTask(boolean addToBackstack) {
            super();
            mAddToBackstack = addToBackstack;
        }

        /**
         * Starts the story header retrieval process.
         * @param params The story header author, story ID, and element ID, in that order.
         * @return A string holding the result of the request.
         */
        @Override
        protected String doInBackground(String...params) {
            String author = params[0];
            String storyId = params[1];
            String elementId = params[2];

            String urlParameters = "author=" + author
                    + "&story_id=" + storyId
                    + "&element_id=" + elementId;
            try {
                return downloadUrl(GET_STORY_ELEMENT_URL, urlParameters, TAG);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Parse JSON
            try {
                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("result");
                if (status.equalsIgnoreCase("success")) {
                    String elementString = jsonObject.getString("storyElement");
                    StoryElement element = StoryElement.parseJson(elementString);
                    Toast.makeText(MainActivity.this, "Success",
                            Toast.LENGTH_SHORT).show();
                    launchStoryElement(element, mAddToBackstack);

                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(MainActivity.this, "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Parsing JSON exception",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
