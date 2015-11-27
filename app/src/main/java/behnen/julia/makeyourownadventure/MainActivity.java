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

import behnen.julia.makeyourownadventure.asyncs.AbstractPostAsyncTask;
import behnen.julia.makeyourownadventure.data.BookmarkedStoryDB;
import behnen.julia.makeyourownadventure.data.CreatedStoryElementDB;
import behnen.julia.makeyourownadventure.data.CreatedStoryHeaderDB;
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
        StoryElementFragment.OnStoryElementInteractionListener,
        CreateEditStoriesFragment.OnCreateEditStoriesInteractionListener,
        CreateNewStoryFragment.OnCreateNewStoryInteractionListener,
        CreatedStoryOverviewFragment.OnCreatedStoryOverviewInteractionListener,
        CreatedStoryElementsFragment.OnCreatedStoryElementsInteractionListener,
        EditStoryElementFragment.OnEditStoryElementInteractionListener {

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

    private void nextStoryElement(String author, String storyId, int elementId,
                                  boolean eraseLast, boolean isOnline) {
        if (isOnline) {
            new StoryGetElementTask(true)
                    .execute(author, storyId, Integer.toString(elementId));
        } else {
            launchLocalStoryElement(author, storyId, elementId, eraseLast, isOnline);
        }
    }

    private void launchStoryElement(StoryElement storyElement, boolean eraseLast,
                                    boolean isOnline) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_container,
                        StoryElementFragment.newInstance(storyElement, isOnline));
        if (eraseLast) {
            getSupportFragmentManager().popBackStack();
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchLocalStoryElement(String author, String storyId, int elementId,
                                         boolean eraseLast, boolean isOnline) {
        StoryElement storyElement = getCreatedStoryElement(author, storyId, elementId);
        launchStoryElement(storyElement, eraseLast, isOnline);
    }

    // DATABASE METHODS

    // BookmarkedStoryDB methods

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

    // CreatedStoryHeaderDB methods

    private boolean addCreatedStoryHeader(StoryHeader storyHeader) {
        CreatedStoryHeaderDB createdStoryHeaderDB = new CreatedStoryHeaderDB(this);
        boolean wasAdded = createdStoryHeaderDB.insertStory(storyHeader);
        createdStoryHeaderDB.closeDB();
        return wasAdded;
    }

    private boolean deleteCreatedStoryHeader(String author, String storyId) {
        CreatedStoryHeaderDB createdStoryHeaderDB = new CreatedStoryHeaderDB(this);
        boolean wasDeleted = createdStoryHeaderDB.deleteStory(author, storyId);
        createdStoryHeaderDB.closeDB();
        return wasDeleted;
    }

    private List<StoryHeader> getCreatedStoryHeaders(String username) {
        CreatedStoryHeaderDB createdStoryHeaderDB = new CreatedStoryHeaderDB(this);
        List<StoryHeader> list = createdStoryHeaderDB.getStoriesByAuthor(username);
        createdStoryHeaderDB.closeDB();
        return list;
    }

    // CreatedStoryElementDB methods

    private boolean addCreatedStoryElement(StoryElement storyElement) {
        CreatedStoryElementDB createdStoryElementDB = new CreatedStoryElementDB(this);
        boolean wasAdded = createdStoryElementDB.insertStoryElement(storyElement);
        createdStoryElementDB.closeDB();
        Log.d(TAG, "element was added " + wasAdded);
        return wasAdded;
    }

    private boolean deleteCreatedStoryElement(String author, String storyId, int elementId) {
        CreatedStoryElementDB createdStoryElementDB = new CreatedStoryElementDB(this);
        boolean wasDeleted = createdStoryElementDB.deleteStoryElement(author, storyId, elementId);
        createdStoryElementDB.closeDB();
        return wasDeleted;
    }

    private StoryElement getCreatedStoryElement(String author, String storyId, int elementId) {
        CreatedStoryElementDB createdStoryElementDB = new CreatedStoryElementDB(this);
        StoryElement storyElement =
                createdStoryElementDB.getStoryElement(author, storyId, elementId);
        createdStoryElementDB.closeDB();
        return storyElement;
    }

    private List<StoryElement> getCreatedStoryElementsByStory(String author, String storyId) {
        CreatedStoryElementDB createdStoryElementDB = new CreatedStoryElementDB(this);
        List<StoryElement> list = createdStoryElementDB.getStoryElementsByStory(author, storyId);
        createdStoryElementDB.closeDB();
        return list;
    }

    // FRAGMENT METHODS

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
    public void onStoryElementChoiceAction(
            String author, String storyId, int elementId, boolean isOnline) {
        nextStoryElement(author, storyId, elementId, true, isOnline);
    }

    @Override
    public void onStoryElementRestartAction(String author, String storyId, boolean isOnline) {
        nextStoryElement(author, storyId, StoryElement.START_ID, true, isOnline);
    }

    @Override
    public void onStoryElementMainMenuAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new MainMenuFragment())
                .commit();    }

    // CreateEditStoriesFragment callback methods

    @Override
    public List<StoryHeader> onCreateEditStoriesGetStories() {
        String username = getCurrentUser();
        return getCreatedStoryHeaders(username);
    }

    @Override
    public void onCreateEditStoriesSelectStory(StoryHeader storyHeader) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container,
                        CreatedStoryOverviewFragment.newInstance(storyHeader))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreateEditStoriesAddStory() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container,
                        CreateNewStoryFragment.newInstance(getCurrentUser()))
                .addToBackStack(null)
                .commit();
    }

    // CreateNewStoryFragment callback methods

    @Override
    public void onCreateNewStorySaveLocalCopy(StoryHeader storyHeader) {
        addCreatedStoryHeader(storyHeader);
        addCreatedStoryElement(new StoryElement(storyHeader.getAuthor(), storyHeader.getStoryId(),
                StoryElement.START_ID, "[Default title]", "", "[Default description]"));
    }

    // CreatedStoryOverviewFragment callback methods

    @Override
    public void onCreatedStoryOverviewFragmentEditHeader(StoryHeader storyHeader) {

    }

    @Override
    public void onCreatedStoryOverviewFragmentEditElements(String author, String storyId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container,
                        CreatedStoryElementsFragment.newInstance(author, storyId))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreatedStoryOverviewFragmentPlayStory(String author, String storyId) {
        launchLocalStoryElement(author, storyId, StoryElement.START_ID, false, false);
    }

    @Override
    public boolean onCreatedStoryOverviewFragmentDeleteStory(String author, String storyId) {
        // TODO check at all points for errors
        boolean success = true;
        List<StoryElement> storyElements = getCreatedStoryElementsByStory(author, storyId);
        for (StoryElement element : storyElements) {
            success = deleteCreatedStoryElement(element.getAuthor(),
                    element.getStoryId(), element.getElementId());
            if (!success) return false;
        }
        success = deleteCreatedStoryHeader(author, storyId);
        return success;
    }

    // CreatedStoryElementsFragment callback methods

    @Override
    public List<StoryElement> onCreatedStoryElementsGetElements(String author, String storyId) {
        List<StoryElement> list = getCreatedStoryElementsByStory(author, storyId);
        return list;
    }

    @Override
    public void onCreatedStoryElementsSelectElement(StoryElement storyElement) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container,
                        EditStoryElementFragment.newInstance(storyElement))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreatedStoryElementsAddElement() {

    }

    // EditStoryElementFragment callback methods

    @Override
    public void onEditStoryElementPreview(StoryElement storyElement) {

    }

    @Override
    public void onEditStoryElementSave(StoryElement storyElement) {

    }

    @Override
    public void onEditStoryElementDelete(StoryElement storyElement) {

    }

    @Override
    public List<StoryElement> onEditStoryElementGetAllElements(String author, String storyId) {
        List<StoryElement> list = getCreatedStoryElementsByStory(author, storyId);
        return list;
    }

    // Shared Async Methods
    /**
     * Downloads a StoryElement from the online database.
     */
    public class StoryGetElementTask extends AbstractPostAsyncTask<String, Void, String> {

        private final boolean mAddToBackstack;

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
                    launchStoryElement(element, mAddToBackstack, true);

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
