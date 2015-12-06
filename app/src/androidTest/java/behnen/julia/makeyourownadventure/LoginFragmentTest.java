package behnen.julia.makeyourownadventure;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * Created by Julia on 12/5/2015.
 */
public class LoginFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private static final String USERNAME = "tester";
    private static final String PASSWORD = "testing";

    private Solo solo;

    public LoginFragmentTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
        solo.unlockScreen();
        if (solo.searchText("Sign out")) {
            solo.clickOnText("Sign out");
        }
    }

    @Override
    public void tearDown() throws Exception {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    public void testRequiredUsername() {
        solo.enterText(1, PASSWORD);
        solo.clickOnButton("Sign in");
        boolean textFound = solo.searchText("Please enter all fields");
        assertTrue("Username required", textFound);
    }

    public void testRequiredPassword() {
        solo.enterText(0, USERNAME);
        solo.clickOnButton("Sign in");
        boolean textFound = solo.searchText("Please enter all fields");
        assertTrue("Password required", textFound);
    }

    public void testIncorrectUsername() {
        solo.enterText(0, "gjka;jslfjsdlfkgajwafklj");
        solo.enterText(1, PASSWORD);
        solo.clickOnButton("Sign in");
        boolean textFound = solo.searchText("Incorrect username or password");
        assertTrue("Incorrect username", textFound);
    }


    public void testIncorrectPassword() {
        solo.enterText(0, USERNAME);
        solo.enterText(1, "bob");
        solo.clickOnButton("Sign in");
        boolean textFound = solo.searchText("Incorrect username or password");
        assertTrue("PIncorrect password", textFound);
    }

    public void testRegistrationAction() {
        solo.clickOnButton("Register");
        boolean textFound = solo.searchText("Confirm password");
        assertTrue("Registering", textFound);
    }

    public void testSuccessfulLogin() {
        solo.enterText(0, USERNAME);
        solo.enterText(1, PASSWORD);
        solo.clickOnButton("Sign in");
        boolean textFound = solo.searchText("About");
        assertTrue("Logged in", textFound);
    }

}
