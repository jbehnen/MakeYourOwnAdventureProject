package behnen.julia.makeyourownadventure;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Julia on 10/30/2015.
 */
public class RegisterFragment extends Fragment {
//    private Course mCourse;

    private static final String URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/php/addUser.php";

    private OnRegisterInteractionListener mCallback;

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

                if (mCallback != null) {
                    mCallback.onRegisterRegisterAction();
                }

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
            mCallback = (OnRegisterInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement OnRegisterInteractionListener");
        }
    }

    public interface OnRegisterInteractionListener {
        void onRegisterRegisterAction();
    }
}
