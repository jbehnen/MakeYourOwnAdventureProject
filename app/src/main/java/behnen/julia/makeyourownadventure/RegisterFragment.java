package behnen.julia.makeyourownadventure;

import android.content.Intent;
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

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mSignInButton;
    private Button mRegisterButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

//        mUsernameEditText = (EditText) v.findViewById(R.id.username);
//        mPasswordEditText = (EditText) v.findViewById(R.id.password);
//        mSignInButton = (Button) v.findViewById(R.id.sign_in_button);
//        mRegisterButton = (Button) v.findViewById(R.id.register_button);
//
//        Intent intent = getActivity().getIntent();
//        String intentUsername = intent.getStringExtra(MainActivity.USERNAME);
//        String intentPassword = intent.getStringExtra(MainActivity.PASSWORD);
//
//        mUsernameEditText.setText(intentUsername);
//        mPasswordEditText.setText(intentPassword);

//        mCourseDB = new CourseDB(v.getContext());

//        mSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String username = mUsernameEditText.getText().toString();
//                String password = mPasswordEditText.getText().toString();
//
//                if (username.length() == 0 || password.length() == 0) {
//                    Toast.makeText(v.getContext(), "Please enter all fields"
//                            , Toast.LENGTH_SHORT)
//                            .show();
//                    return;
//                }
//
//                Intent intent = new Intent(null, MainActivity.class);
//                intent.putExtra(MainActivity.SHOW, MainActivity.SHOW_MAIN_MENU);
////                if (prereqs.length() == 0) prereqs = null;
////                mCourse = new Course(username, password, longDesc, prereqs);
////
////                try {
////                    mCourseDB.insert(username, password, longDesc, prereqs);
////                } catch (Exception e) {
////                    Toast.makeText(v.getContext(), e.toString(), Toast.LENGTH_LONG)
////                            .show();
////
////                    return;
////                }
////                Toast.makeText(v.getContext(), "Course added successfully!", Toast.LENGTH_SHORT)
////                        .show();
//
//            }
//        });
//
//        mRegisterButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String username = mUsernameEditText.getText().toString();
//                String password = mPasswordEditText.getText().toString();
//
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                intent.putExtra(MainActivity.SHOW, MainActivity.SHOW_MAIN_MENU);
//                getActivity().startActivity(intent);
//            }
//        });

        return v;
    }
}
