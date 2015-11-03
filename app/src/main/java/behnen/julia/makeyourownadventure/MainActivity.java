package behnen.julia.makeyourownadventure;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import behnen.julia.makeyourownadventure.model.StoryHeader;

public class MainActivity extends AppCompatActivity implements
        SignInFragment.SignInInteractionListener,
        RegisterFragment.RegisterInteractionListener,
        MainMenuFragment.MainMenuInteractionListener,
        MyStoriesFragment.MyStoriesInteractionListener,
        DownloadedStoriesListFragment.OnDownloadedStoriesListInteractionListener,
        DownloadStoryFragment.OnDownloadStoryInteractionListener {

    private static final String TAG = "MainActivity";

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

    // Sign In callback methods

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

    // Register callback methods

    @Override
    public void onRegisterRegisterAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new MainMenuFragment())
                .commit();
    }

    // Main Menu callback methods

    @Override
    public void onMainMenuContinueStoryAction() {

    }

    @Override
    public void onMainMenuDownloadedStoriesAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new DownloadStoryFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMainMenuMyStoriesAction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new MyStoriesFragment())
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

    @Override
    public void onDownloadedStoriesListStorySelected(StoryHeader storyHeader) {

    }

    @Override
    public void onDownloadedStoriesListDownloadNewStory() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new DownloadStoryFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDownloadStoryDownloadSuccess(String serializedStory) {
//        // placeholder until database up and running
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.main_fragment_container,
//                        DownloadedStoriesListFragment.newInstance(serializedStory))
//                .commit();
////        getSupportFragmentManager().popBackStackImmediate();
    }
}
