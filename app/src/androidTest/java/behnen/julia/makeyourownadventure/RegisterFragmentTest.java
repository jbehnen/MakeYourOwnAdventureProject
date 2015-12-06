package behnen.julia.makeyourownadventure;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import java.util.Random;

/**
 * Created by Julia on 12/5/2015.
 */
public class RegisterFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email@email.email";

    private Solo solo;

    public RegisterFragmentTest() {
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
        solo.waitForText("Register");
        solo.clickOnText("Register");
    }

    @Override
    public void tearDown() throws Exception {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    public void testRequiredUsername() {
        solo.enterText(1, PASSWORD);
        solo.enterText(2, PASSWORD);
        solo.enterText(3, EMAIL);
        solo.clickOnButton("Register");
        boolean textFound = solo.searchText("Please enter all fields");
        assertTrue("Username required", textFound);
    }

    public void testRequiredPassword() {
        solo.enterText(0, USERNAME);
        solo.enterText(2, PASSWORD);
        solo.enterText(3, EMAIL);
        solo.clickOnButton("Register");
        boolean textFound = solo.searchText("Please enter all fields");
        assertTrue("Password required", textFound);
    }

    public void testRequiredConfirmPassword() {
        solo.enterText(0, USERNAME);
        solo.enterText(1, PASSWORD);
        solo.enterText(3, EMAIL);
        solo.clickOnButton("Register");
        boolean textFound = solo.searchText("Please enter all fields");
        assertTrue("Confirmed password required", textFound);
    }

    public void testRequiredEmail() {
        solo.enterText(0, USERNAME);
        solo.enterText(1, PASSWORD);
        solo.enterText(2, PASSWORD);
        solo.clickOnButton("Register");
        boolean textFound = solo.searchText("Please enter all fields");
        assertTrue("Email required", textFound);
    }

    public void testRequiredMatchingPasswords() {
        solo.enterText(0, USERNAME);
        solo.enterText(1, PASSWORD);
        solo.enterText(2, "password1");
        solo.enterText(3, EMAIL);
        solo.clickOnButton("Register");
        boolean textFound = solo.searchText("The passwords do not match");
        assertTrue("Email invalid", textFound);
    }

    public void testInvalidUsername() {
        solo.enterText(0, "bobi");
        solo.enterText(1, PASSWORD);
        solo.enterText(2, PASSWORD);
        solo.enterText(3, EMAIL);
        solo.clickOnButton("Register");
        boolean textFound = solo.searchText("This username is invalid");
        assertTrue("Username too short", textFound);
    }

    public void testInvalidPassword() {
        solo.enterText(0, USERNAME);
        solo.enterText(1, "bobi");
        solo.enterText(2, PASSWORD);
        solo.enterText(3, EMAIL);
        solo.clickOnButton("Register");
        boolean textFound = solo.searchText("This password is too short");
        assertTrue("Password too short", textFound);
    }

    public void testInvalidEmail() {
        solo.enterText(0, USERNAME);
        solo.enterText(1, PASSWORD);
        solo.enterText(2, PASSWORD);
        solo.enterText(3, "personatperson.com");
        solo.clickOnButton("Register");
        boolean textFound = solo.searchText("This email is invalid");
        assertTrue("Email invalid", textFound);
    }

    public void testMaxUsernameLength() {
        solo.enterText(0, "thisusernameisovertwentyfivecharacterslong");
        solo.enterText(1, PASSWORD);
        solo.enterText(2, PASSWORD);
        solo.enterText(3, EMAIL);
        boolean originalName = solo.searchText("thisusernameisovertwentyfivecharacterslong");
        boolean twentyFiveRemain = solo.searchText("thisusernameisovertwentyf");
        assertFalse("Original name no longer present", originalName);
        assertTrue("Truncated name present", twentyFiveRemain);
    }

    public void testMaxPasswordLength() {
        solo.enterText(0, USERNAME);
        solo.enterText(1, "fivefivefivefivefivefive!!!");
        solo.enterText(2, PASSWORD);
        solo.enterText(3, EMAIL);
        boolean originalPassword = solo.searchText("fivefivefivefivefivefive!!!");
        boolean twentyFiveRemain = solo.searchText("fivefivefivefivefivefive!");
        assertFalse("Original password no longer present", originalPassword);
        assertTrue("Truncated password present", twentyFiveRemain);
    }

    public void testMaxEmailLength() {
        solo.enterText(0, USERNAME);
        solo.enterText(1, PASSWORD);
        solo.enterText(2, PASSWORD);
        solo.enterText(3, "aaaaabbbbbcccccdddddeeeeefffffggggghhhhhiiiiijjjjjkkkkk");
        boolean originalEmail =
                solo.searchText("aaaaabbbbbcccccdddddeeeeefffffggggghhhhhiiiiijjjjjkkkkk");
        boolean fiftyRemain =
                solo.searchText("aaaaabbbbbcccccdddddeeeeefffffggggghhhhhiiiiijjjjj");
        assertFalse("Original email no longer present", originalEmail);
        assertTrue("Truncated email present", fiftyRemain);
    }

    public void testSuccessfulRegistration() {
        Random rand = new Random();
        int name = rand.nextInt();
        solo.enterText(0, Integer.toString(name));
        solo.enterText(1, PASSWORD);
        solo.enterText(2, PASSWORD);
        solo.enterText(3, EMAIL);
        solo.clickOnButton("Register");
        boolean textFound = solo.searchText("Sign in");
        assertTrue("Signed in", textFound);
    }

}
