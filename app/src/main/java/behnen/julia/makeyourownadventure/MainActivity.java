package behnen.julia.makeyourownadventure;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import behnen.julia.makeyourownadventure.asyncs.AbstractDownloadStoryElementTask;
import behnen.julia.makeyourownadventure.data.BookmarkedStoryDB;
import behnen.julia.makeyourownadventure.data.CreatedStoryElementDB;
import behnen.julia.makeyourownadventure.data.CreatedStoryHeaderDB;
import behnen.julia.makeyourownadventure.data.CurrentElementDB;
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

    // TODO: fix lifecycle on fragments with editable fields/spinners

    private static final String TAG = "MainActivity";

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), MODE_PRIVATE);

        if (savedInstanceState ==  null) {
            boolean loggedIn = mSharedPreferences.getBoolean(
                    getString(R.string.LOGGEDIN), false);
            if (findViewById(R.id.main_fragment_container) != null) {
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                if (!loggedIn) {
                    Fragment loginFragment = new SignInFragment();
                    fragmentTransaction.replace(R.id.main_fragment_container, loginFragment)
                            .commit();
                } else {
                    Fragment menuFragment = new MainMenuFragment();
                    fragmentTransaction.replace(R.id.main_fragment_container, menuFragment)
                            .commit();
                }
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

        return super.onOptionsItemSelected(item);
    }

    // Shared methods

    /**
     * Returns the username of the logged-in user.
     * @return The username of the logged-in user.
     */
    private String getCurrentUser() {
        return mSharedPreferences.getString(getString(R.string.USERNAME), null);
    }

    /**
     * Display the given story element when another story element is currently displayed.
     *
     * @param author The author of the story element.
     * @param storyId The storyId of the story element.
     * @param elementId The elementId of the story element.
     * @param eraseLast True if the current fragment should be removed from the back stack,
     *                  false otherwise.
     * @param isOnline True if the story element is stored online, false if it is stored locally.
     */
    private void nextStoryElement(String author, String storyId, int elementId,
                                  boolean eraseLast, boolean isOnline) {
        if (isOnline) {
            new StoryGetElementTask(true)
                    .execute(author, storyId, Integer.toString(elementId));
        } else {
            launchLocalStoryElement(author, storyId, elementId, eraseLast);
        }
    }

    /**
     * Displays the given story element.
     *
     * @param storyElement The story element to be displayed.
     * @param eraseLast True if the current fragment should be removed from the back stack,
     *                  false otherwise.
     * @param isOnline True if the story element is stored online, false if it is stored locally.
     * @param isActive True if the story element should be interactive (playable),
     *                 false if it is a demo.
     */
    private void launchStoryElement(StoryElement storyElement, boolean eraseLast,
                                    boolean isOnline, boolean isActive) {
        if (isOnline && isActive) {
            updateCurrentStoryElement(storyElement.getAuthor(), storyElement.getStoryId(),
                    storyElement.getElementId());
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_container,
                        StoryElementFragment.newInstance(storyElement, isOnline, isActive));
        if (eraseLast) {
            getSupportFragmentManager().popBackStack();
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Displays a story element stored locally.
     * @param author The author of the story element.
     * @param storyId The storyId of the story element.
     * @param elementId The elementId of the story element.
     * @param eraseLast True if the current fragment should be removed from the back stack,
     *                  false otherwise.
     */
    private void launchLocalStoryElement(String author, String storyId, int elementId,
                                         boolean eraseLast) {
        StoryElement storyElement = getCreatedStoryElement(author, storyId, elementId);
        launchStoryElement(storyElement, eraseLast, false, true);
    }

    // DATABASE METHODS

    // CurrentElementDB methods

    /**
     * Adds an entry for the current user to the current element database.
     *
     * @return True if the user was added successfully, false otherwise.
     */
    private boolean addCurrentElement() {
        CurrentElementDB currentElementDB = new CurrentElementDB(this);
        String username = getCurrentUser();
        boolean wasAdded = currentElementDB.insertCurrentElement(username);
        currentElementDB.closeDB();
        return wasAdded;
    }

    /**
     * Updates the user's current story element stored in the database.
     *
     * @param author The author of the story element.
     * @param storyId The storyId of the story element.
     * @param elementId The elementId of the story element.
     * @return True if the entry was updated successfully, false otherwise.
     */
    private boolean updateCurrentStoryElement(String author, String storyId, int elementId) {
        CurrentElementDB currentElementDB = new CurrentElementDB(this);
        String username = getCurrentUser();
        boolean wasUpdated =
                currentElementDB.updateCurrentElement(username, author, storyId, elementId);
        currentElementDB.closeDB();
        return wasUpdated;
    }

    /**
     * Clears the user's current story element entry stored in the database.
     *
     * @return True if the entry was cleared successfully, false otherwise.
     */
    private boolean clearCurrentStoryElement() {
        CurrentElementDB currentElementDB = new CurrentElementDB(this);
        String username = getCurrentUser();
        boolean wasCleared = currentElementDB.clearCurrentElement(username);
        currentElementDB.closeDB();
        return wasCleared;
    }

    /**
     * Returns the current user's current story element data stored in the database.
     * @return The current user's current story element data stored in the database,
     *         where index 0 is the author, 1 is the storyId, and 2 is the elementId.
     */
    private String[] getCurrentStoryElement() {
        CurrentElementDB currentElementDB = new CurrentElementDB(this);
        String username = getCurrentUser();
        String[] preferences = currentElementDB.getCurrentElement(username);
        currentElementDB.closeDB();
        return preferences;
    }

    // BookmarkedStoryDB methods

    /**
     * Adds a story header to the bookmarked stories database.
     * @param storyHeader The story header to add.
     * @return True if the story header was successfully added, false otherwise.
     */
    private boolean addBookmarkedStory(StoryHeader storyHeader) {
        BookmarkedStoryDB bookmarkedStoryDB = new BookmarkedStoryDB(this);
        String username = getCurrentUser();
        boolean wasAdded = bookmarkedStoryDB.insertStory(username, storyHeader);
        bookmarkedStoryDB.closeDB();
        return wasAdded;
    }

    /**
     * Deletes a story header from the bookmarked stories database.
     * @param author The author of the story header.
     * @param storyId The storyId of the story header.
     * @return True if the story header was successfully deleted, false otherwise.
     */
    private boolean deleteBookmarkedStory(String author, String storyId) {
        BookmarkedStoryDB bookmarkedStoryDB = new BookmarkedStoryDB(this);
        String username = getCurrentUser();
        boolean wasDeleted = bookmarkedStoryDB.deleteStory(username, author, storyId);
        bookmarkedStoryDB.closeDB();
        return wasDeleted;
    }

    /**
     * Returns all the story headers of the stories that have been bookmarked by the current user.
     * @return All the story headers of the stories that have been bookmarked by the current user.
     */
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

    private boolean updateCreatedStoryHeader(StoryHeader storyHeader) {
        CreatedStoryHeaderDB createdStoryHeaderDB = new CreatedStoryHeaderDB(this);
        boolean wasAdded = createdStoryHeaderDB.updateStoryHeader(storyHeader);
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

    private boolean isCreatedStoryFinal(String author, String storyId) {
        CreatedStoryHeaderDB createdStoryHeaderDB = new CreatedStoryHeaderDB(this);
        boolean isFinal = createdStoryHeaderDB.isStoryFinal(author, storyId);
        createdStoryHeaderDB.closeDB();
        return isFinal;
    }

    private boolean setCreatedStoryFinal(String author, String storyId, boolean isFinal) {
        CreatedStoryHeaderDB createdStoryHeaderDB = new CreatedStoryHeaderDB(this);
        boolean success = createdStoryHeaderDB.setStoryFinal(author, storyId, isFinal);
        createdStoryHeaderDB.closeDB();
        return success;
    }

    // CreatedStoryElementDB methods

    private boolean addCreatedStoryElement(StoryElement storyElement) {
        CreatedStoryElementDB createdStoryElementDB = new CreatedStoryElementDB(this);
        boolean wasAdded = createdStoryElementDB.insertStoryElement(storyElement);
        createdStoryElementDB.closeDB();
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

    private boolean updateCreatedStoryElement(StoryElement storyElement) {
        CreatedStoryElementDB createdStoryElementDB = new CreatedStoryElementDB(this);
        boolean wasDeleted = createdStoryElementDB.updateStoryElement(storyElement);
        createdStoryElementDB.closeDB();
        return wasDeleted;
    }

    private int getNextCreatedStoryElementId(String author, String storyId) {
        CreatedStoryElementDB createdStoryElementDB = new CreatedStoryElementDB(this);
        int nextId = createdStoryElementDB.getNextElementId(author, storyId);
        createdStoryElementDB.closeDB();
        return nextId;
    }

    private boolean hasStoryElements(String author, String storyId) {
        CreatedStoryElementDB createdStoryElementDB = new CreatedStoryElementDB(this);
        boolean wasDeleted = createdStoryElementDB.hasStoryElements(author, storyId);
        createdStoryElementDB.closeDB();
        return wasDeleted;
    }

    // FRAGMENT METHODS

    // SignInFragment callback methods

    @Override
    public void onSignInSignInAction(String username) {
        mSharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(getString(R.string.USERNAME), username);
        editor.putBoolean(getString(R.string.LOGGEDIN), true);
        editor.commit();

        addCurrentElement();
        // TODO: make sure that sign-in/insert doesn't crash app

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
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
    public String[] onMainMenuResume() {
        return getCurrentStoryElement();
    }

    @Override
    public void onMainMenuContinueStoryAction(StoryElement storyElement) {
        String[] element = getCurrentStoryElement();
        new StoryGetElementTask(true)
                .execute(element[0], element[1], element[2]);
    }

    @Override
    public void onMainMenuBookmarkedStoriesAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new BookmarkedStoriesFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMainMenuCreateEditStoriesAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new CreateEditStoriesFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMainMenuAboutAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new AboutFragment())
                .addToBackStack(null)
                .commit();
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
        String[] currentStory = getCurrentStoryElement();
        // if active story is being deleted, clear it from user preferences
        if (currentStory[0].equals(author) && currentStory[1].equals(storyId)) {
            clearCurrentStoryElement();
        }

        return deleteBookmarkedStory(author, storyId);
    }

    // StoryElementFragment callback methods

    @Override
    public void onStoryElementChoiceAction(
            String author, String storyId, int elementId, boolean isOnline) {
        Log.d(TAG, "onStoryElementChoiceAction");
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
                .commit();
    }

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
                StoryElement.START_ID));
    }

    // CreatedStoryOverviewFragment callback methods

    @Override
    public boolean onCreatedStoryOverviewUpdateHeader(StoryHeader storyHeader) {
        return updateCreatedStoryHeader(storyHeader);
    }

    @Override
    public void onCreatedStoryOverviewEditElements(String author, String storyId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container,
                        CreatedStoryElementsFragment.newInstance(author, storyId))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreatedStoryOverviewPlayStory(String author, String storyId) {
        launchLocalStoryElement(author, storyId, StoryElement.START_ID, false);
    }

    @Override
    public boolean onCreatedStoryOverviewDeleteLocalStory(String author, String storyId) {
        boolean deleted = true;
        List<StoryElement> elements = getCreatedStoryElementsByStory(author, storyId);
        for (StoryElement elem: elements) {
            deleted &= deleteCreatedStoryElement(author, storyId, elem.getElementId());
        }
        if (deleted) {
            deleted &= deleteCreatedStoryHeader(author, storyId);
        }
        return deleted;
    }

    @Override
    public void onCreatedStoryOverviewOnCompletedUpload(StoryHeader storyHeader) {
        addBookmarkedStory(storyHeader);
        deleteCreatedStoryHeader(storyHeader.getAuthor(), storyHeader.getStoryId());
    }

    @Override
    public boolean onCreatedStoryOverviewDeleteStoryElement(
            String author, String storyId, int elementId) {
        return deleteCreatedStoryElement(author, storyId, elementId);
    }

    @Override
    public List<StoryElement> onCreatedStoryOverviewGetStoryElements(
            String author, String storyId) {
        return getCreatedStoryElementsByStory(author, storyId);
    }

    @Override
    public boolean onCreatedStoryOverviewStoryElementsExist(String author, String storyId) {
        return hasStoryElements(author, storyId);
    }


    @Override
    public boolean onCreatedStoryIsStoryFinal(String author, String storyId) {
        return isCreatedStoryFinal(author, storyId);
    }

    @Override
    public boolean onCreatedStorySetStoryFinal(String author, String storyId) {
        return setCreatedStoryFinal(author, storyId, true);
    }

    // CreatedStoryElementsFragment callback methods

    @Override
    public List<StoryElement> onCreatedStoryElementsGetElements(String author, String storyId) {
        return getCreatedStoryElementsByStory(author, storyId);
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
    public void onCreatedStoryElementsAddElement(String author, String storyId) {
        StoryElement storyElement = new StoryElement(author, storyId,
                getNextCreatedStoryElementId(author, storyId));
        addCreatedStoryElement(storyElement);
        onCreatedStoryElementsSelectElement(storyElement);
    }

    // EditStoryElementFragment callback methods

    @Override
    public void onEditStoryElementPreview(StoryElement storyElement) {
        launchStoryElement(storyElement, false, false, false);
    }

    @Override
    public boolean onEditStoryElementSave(StoryElement storyElement) {
        return updateCreatedStoryElement(storyElement);
    }

    @Override
    public boolean onEditStoryElementDelete(StoryElement storyElement) {
        return deleteCreatedStoryElement(storyElement.getAuthor(), storyElement.getStoryId(),
                storyElement.getElementId());
    }

    @Override
    public List<StoryElement> onEditStoryElementGetAllElements(String author, String storyId) {
        List<StoryElement> list = getCreatedStoryElementsByStory(author, storyId);
        return list;
    }

    // Shared Async Methods

    private class StoryGetElementTask extends AbstractDownloadStoryElementTask {

        private final boolean mAddToBackstack;

        public StoryGetElementTask(boolean addToBackstack) {
            super();
            mAddToBackstack = addToBackstack;
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
                    launchStoryElement(element, mAddToBackstack, true, true);

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
