package behnen.julia.makeyourownadventure;

import android.app.Activity;
import android.os.AsyncTask;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Julia on 10/30/2015.
 */
public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";
    private static final String URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/login.php";

    private OnSignInInteractionListener mCallback;

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mSignInButton;
    private Button mRegisterButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mUsernameEditText = (EditText) v.findViewById(R.id.username);
        mPasswordEditText = (EditText) v.findViewById(R.id.password);
        mSignInButton = (Button) v.findViewById(R.id.sign_in_button);
        mRegisterButton = (Button) v.findViewById(R.id.register_button);

//        mCourseDB = new CourseDB(v.getContext());

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsernameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                if (username.length() == 0 || password.length() == 0) {
                    Toast.makeText(v.getContext(), "Please enter all fields"
                            , Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                attemptLogin();

//                if (prereqs.length() == 0) prereqs = null;
//                mCourse = new Course(username, password, longDesc, prereqs);
//
//                try {
//                    mCourseDB.insert(username, password, longDesc, prereqs);
//                } catch (Exception e) {
//                    Toast.makeText(v.getContext(), e.toString(), Toast.LENGTH_LONG)
//                            .show();
//
//                    return;
//                }
//                Toast.makeText(v.getContext(), "Course added successfully!", Toast.LENGTH_SHORT)
//                        .show();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onSignInRegisterAction();
                }
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnSignInInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement OnSignInInteractionListener");
        }
    }

    public void attemptLogin() {

        // Reset errors.
        mUsernameEditText.setError(null);
        mPasswordEditText.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordEditText.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameEditText.setError(getString(R.string.error_field_required));
            focusView = mUsernameEditText;
            cancel = true;
//        } else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_username));
//            focusView = mEmailView;
//            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        } else {
            String loginUrl = URL + "?username=" + mUsernameEditText.getText().toString()
                    + "&password=" + mPasswordEditText.getText().toString();
            new UserSignInTask(username, password)
                    .execute(loginUrl);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserSignInTask extends AsyncTask<String, Void, String> {

        private final String mEmail;
        private final String mPassword;

        UserSignInTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(String...urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid";
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // only display the first 500 chars of the retrieved web page content
            int len = 500;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                int response = conn.getResponseCode();
                Log.d(TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                Log.d(TAG, "The string is: " + contentAsString);
                return contentAsString;
            } catch(Exception e) {
                Log.d(TAG, "Something happened: " + e.getMessage());
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return null;
        }

        // Reads an InputStream and converts it to a String
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
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
                    if (mCallback != null) {
                        mCallback.onSignInSignInAction();
                    }
                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getActivity(), "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                }
                getFragmentManager().popBackStackImmediate();
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception" + e.getMessage());
                Toast.makeText(getActivity(), "Parsing JSON exception: " + s,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnSignInInteractionListener {
        void onSignInSignInAction();
        void onSignInRegisterAction();
    }
}
