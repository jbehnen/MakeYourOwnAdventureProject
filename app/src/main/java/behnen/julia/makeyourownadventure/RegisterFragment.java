package behnen.julia.makeyourownadventure;

import android.app.Activity;
import android.content.Context;
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
 * Created by Julia on 10/30/2015.
 */
public class RegisterFragment extends Fragment {
//    private Course mCourse;

    private static final String TAG = "RegisterFragment";
    private static final String URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/addUser.php";

    /**
     * The context which implements the interface methods.
     */
    private RegisterInteractionListener mCallback;

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;
    private EditText mEmailEditText;
    private Button mRegisterButton;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface RegisterInteractionListener {
        void onRegisterRegisterAction();
    }

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

            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (RegisterInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement RegisterInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
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
            new UserRegisterTask().execute(username, Helper.encryptPassword(password), email);
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
     * Represents an asynchronous login task used to register
     * the user.
     */
    public class UserRegisterTask extends AbstractPostAsyncTask<String, Void, String> {

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
