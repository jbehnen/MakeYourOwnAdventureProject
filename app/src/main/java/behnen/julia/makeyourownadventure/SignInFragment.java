package behnen.julia.makeyourownadventure;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import behnen.julia.makeyourownadventure.asyncs.AbstractPostAsyncTask;
import behnen.julia.makeyourownadventure.support.Helper;

/**
 * A fragment that allows a new user to sign in to the app.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class SignInFragment extends Fragment {

    /**
     * The tag used for logging.
     */
    private static final String TAG = "SignInFragment";
    /**
     * The URL for login requests.
     */
    private static final String URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/login.php";

    /**
     * The context which implements the interface methods.
     */
    private OnSignInInteractionListener mCallback;

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    private Button mSignInButton;
    private Button mRegisterButton;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnSignInInteractionListener {
        /**
         * The callback triggered when the user successfully signs in.
         * @param username The username of the user.
         */
        void onSignInSignInAction(String username);

        /**
         * The callback triggered when the user wants to register a new account.
         */
        void onSignInRegisterAction();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    /**
     * Resumes fragment and updates activity action bar title.
     *
     * Action bar title update code adapted from
     * http://stackoverflow.com/questions/13472258/
     * handling-actionbar-title-with-the-fragment-back-stack
     */
    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().setTitle(R.string.sign_in_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mUsernameEditText = (EditText) v.findViewById(R.id.username);
        mPasswordEditText = (EditText) v.findViewById(R.id.password);

        mSignInButton = (Button) v.findViewById(R.id.sign_in_button);
        mRegisterButton = (Button) v.findViewById(R.id.register_button);

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
                enableButtons(false);
                attemptLogin();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnSignInInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnSignInInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /**
     * Enables or disables the log in and register buttons.
     * @param areEnabled True if the buttons should be enabled or
     *                   false if they should be disabled.
     */
    private void enableButtons(boolean areEnabled) {
        mSignInButton.setEnabled(areEnabled);
        mRegisterButton.setEnabled(areEnabled);
    }

    /**
     * Attempts to log in the user.
     */
    public void attemptLogin() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        new UserSignInTask().execute(username, Helper.encryptString(password));
    }

    /**
     * Represents an asynchronous login task used to authenticate the user.
     */
    public class UserSignInTask extends AbstractPostAsyncTask<String, Void, String> {

        /**
         * Starts the login process.
         * @param params The username and password, in that order.
         * @return A string holding the result of the request.
         */
        @Override
        protected String doInBackground(String...params) {
            String urlParameters = "username=" + params[0]
                    + "&password=" + params[1];
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
                    enableButtons(true);
                    if (mCallback != null) {
                        mCallback.onSignInSignInAction(mUsernameEditText.getText().toString());
                    }
                } else {
                    enableButtons(true);
                    String reason = jsonObject.getString("error");
                    Toast.makeText(getActivity(), "Failed: " + reason,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                enableButtons(true);
                Log.d(TAG, "Parsing JSON Exception" + e.getMessage());
                Toast.makeText(getActivity(),
                        getActivity().getResources().getString(R.string.async_error),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
