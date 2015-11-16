package behnen.julia.makeyourownadventure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import behnen.julia.makeyourownadventure.support.Helper;

/**
 * A fragment that allows a new user to register with the app.
 *
 * @author Julia Behnen
 * @version November 4, 2015
 */
public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    /**
     * The URL for user registration requests.
     */
    private static final String URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/addUser.php";

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;
    private EditText mEmailEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        mUsernameEditText = (EditText) v.findViewById(R.id.username);
        mPasswordEditText = (EditText) v.findViewById(R.id.password);
        mPasswordConfirmEditText = (EditText) v.findViewById(R.id.password_confirm);
        mEmailEditText = (EditText) v.findViewById(R.id.email);

        Button mRegisterButton = (Button) v.findViewById(R.id.register_button);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsernameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                String confirmPassword = mPasswordConfirmEditText.getText().toString();
                String email = mEmailEditText.getText().toString();

                if (username.length() == 0 || password.length() == 0
                        || confirmPassword.length() == 0 || email.length() == 0) {
                    Toast.makeText(v.getContext(), "Please enter all fields"
                            , Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                attemptRegister();

            }
        });

        return v;
    }

    /**
     * Attempts to register the new user.
     */
    private void attemptRegister() {
        // Reset errors.
        mUsernameEditText.setError(null);
        mPasswordEditText.setError(null);
        mPasswordConfirmEditText.setError(null);
        mEmailEditText.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mPasswordConfirmEditText.getText().toString();
        String email = mEmailEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameEditText.setError(getString(R.string.error_field_required));
            focusView = mEmailEditText;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mEmailEditText.setError(getString(R.string.error_invalid_username));
            focusView = mEmailEditText;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError(getString(R.string.error_field_required));
            focusView = mPasswordEditText;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordEditText.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEditText;
            cancel = true;
        }

        // Check for valid password confirmation
        if (TextUtils.isEmpty(confirmPassword)) {
            mPasswordConfirmEditText.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfirmEditText;
            cancel = true;
        } else if (!password.equals(confirmPassword)) {
            mPasswordConfirmEditText.setError(getString(R.string.error_invalid_password_confirm));
            focusView = mPasswordConfirmEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            focusView = mEmailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailEditText.setError(getString(R.string.error_invalid_email));
            focusView = mEmailEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        } else {
            new UserRegisterTask().execute(username, Helper.encryptPassword(password), email);
        }
    }

    /**
     * Returns true if the username is valid, false otherwise.
     * @param username The username.
     * @return True if the username is valid, false otherwise.
     */
    private boolean isUsernameValid(String username) {
        return username.length() > 4;
    }

    /**
     * Returns true if the email is valid, false otherwise.
     * @param email The email.
     * @return True if the email is valid, false otherwise.
     */
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * Returns true if the password is valid, false otherwise.
     * @param password The password.
     * @return True if the password is valid, false otherwise.
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Represents an asynchronous login task used to register the user.
     */
    public class UserRegisterTask extends AbstractPostAsyncTask<String, Void, String> {

        /**
         * Starts the user registration process.
         * @param params The username, password, and email, in that order.
         * @return A string holding the result of the request.
         */
        @Override
        protected String doInBackground(String...params) {
            String urlParameters = "username=" + params[0]
                    + "&password=" + params[1]
                    + "&email=" + params[2];
            try {
                return downloadUrl(URL, urlParameters, TAG);
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
                    Toast.makeText(getActivity(), "Success",
                            Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getActivity(), "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception" + e.getMessage());
                Toast.makeText(getActivity(), "Parsing JSON exception: " + s,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
