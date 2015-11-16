package behnen.julia.makeyourownadventure;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

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
        SignInFragment.SignInInteractionListener,
        RegisterFragment.RegisterInteractionListener,
        MainMenuFragment.MainMenuInteractionListener,
        BookmarkedStoriesFragment.OnBookmarkedStoriesInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new SignInFragment();
        fm.beginTransaction()
                .add(R.id.main_fragment_container, fragment)
                .commit();
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

    private void downloadStoryElement(String author, String storyId, int elementId) {

    }

    private void displayStoryElement(StoryElement storyElement) {
//        if (storyElement.isEnding()) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.main_fragment_container, new EndingFragment())
//                    .commit();
//        } else {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.main_fragment_container, new ChoiceFragment())
//                    .commit();
//        }
    }

    // Bookmarked Stories database methods

    public static boolean addBookmarkedStory(Context c, StoryHeader storyHeader) {
        BookmarkedStoryDB bookmarkedStoryDB = new BookmarkedStoryDB(c);
        // TODO: get username from shared preferences
        boolean wasAdded = bookmarkedStoryDB.insertStory("database_test_user", storyHeader);
        bookmarkedStoryDB.closeDB();
        return wasAdded;
    }

    public static List<StoryHeader> getBookmarkedStories(Context c) {
        BookmarkedStoryDB bookmarkedStoryDB = new BookmarkedStoryDB(c);
        // TODO: get username from shared preferences
        List<StoryHeader> list = bookmarkedStoryDB.getStoriesByUsername("database_test_user");
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

    // RegisterFragment callback methods

    @Override
    public void onRegisterRegisterAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new MainMenuFragment())
                .commit();
    }

    // MainMenuFragment callback methods

    @Override
    public void onMainMenuContinueStoryAction() {

    }

    @Override
    public void onMainMenuStorySoFarAction() {

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

    }

    @Override
    public void onMainMenuSignOutAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new SignInFragment())
                .commit();
    }

    // BookmarkedStoriesFragment callback methods

    @Override
    public void onBookmarkedStoriesSelectStory(StoryHeader storyHeader) {
    }

    @Override
    public void onBookmarkedStoriesAddStory() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new GetNewStoryFragment())
                .addToBackStack(null)
                .commit();
    }
}
