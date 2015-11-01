package behnen.julia.makeyourownadventure;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements
        SignInFragment.OnSignInInteractionListener,
        RegisterFragment.OnRegisterInteractionListener,
        MainMenuFragment.OnMainMenuInteractionListener {

    public static final String SHOW = "show";
    public static final String SHOW_SIGN_IN = "SignIn";
    public static final String SHOW_REGISTER = "Register";
    public static final String SHOW_MAIN_MENU = "MainMenu";
    public static final String ABOUT = "About";

    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";

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

    }

    @Override
    public void onMainMenuMyStoriesAction() {

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
}
