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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import behnen.julia.makeyourownadventure.model.Helper;

/**
 * Created by Julia on 10/30/2015.
 */
public class RegisterFragment extends Fragment {
//    private Course mCourse;

    private static final String TAG = "RegisterFragment";
    private static final String URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/addUser.php";

    private RegisterInteractionListener mCallback;

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;
    private EditText mEmailEditText;
    private Button mRegisterButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        mUsernameEditText = (EditText) v.findViewById(R.id.username);
        mPasswordEditText = (EditText) v.findViewById(R.id.password);
        mPasswordConfirmEditText = (EditText) v.findViewById(R.id.password_confirm);
        mEmailEditText = (EditText) v.findViewById(R.id.email);
        mRegisterButton = (Button) v.findViewById(R.id.register_button);

//        mCourseDB = new CourseDB(v.getContext());

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

//                if (mCallback != null) {
//                    mCallback.onRegisterRegisterAction();
//                }

                // add the password checks; transfer/adapt logic from sign in


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

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (RegisterInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement RegisterInteractionListener");
        }
    }

    public interface RegisterInteractionListener {
        void onRegisterRegisterAction();
    }

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
//            String registerUrl = URL + "?username=" + username
//                    + "&email=" + email
//                    + "&password=" + Helper.hashPassword(password);
            new UserRegisterTask().execute(username, Helper.hashPassword(password), email);
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() > 4;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...params) {
            try {
                return downloadUrl(params[0], params[1], params[2]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid";
            }
        }

        private String downloadUrl(String username, String password, String email)
                throws IOException {
            InputStream is = null;
            // only display the first 500 chars of the retrieved web page content
            int len = 500;

            // Post request approach adapted from
            // http://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
            try {
                URL url = new URL(URL);
                String urlParameters = "username=" + username
                        + "&password=" + password
                        + "&email=" + email;
                byte[] postData = urlParameters.getBytes();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
                conn.setDoInput(true);
                conn.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
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
                        mCallback.onRegisterRegisterAction();
                    }
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
