package behnen.julia.makeyourownadventure.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import behnen.julia.makeyourownadventure.R;

/**
 * Created by Julia on 11/24/2015.
 */
public class EditStoryHeaderDialogFragment extends DialogFragment {

    public static final String EXTRA_TITLE = "behnen.julia.makeyourownadventrue.title";
    public static final String EXTRA_DESCRIPTION = "behnen.julia.makeyourownadventrue.description";

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";


    public static EditStoryHeaderDialogFragment newInstance(String title, String description) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);

        EditStoryHeaderDialogFragment fragment = new EditStoryHeaderDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_edit_story_header, null);

        String title = getArguments().getString(ARG_TITLE);
        String description = getArguments().getString(ARG_DESCRIPTION);

        final EditText titleEditText =
                ((EditText) view.findViewById(R.id.edit_story_header_title_edit_text));
        titleEditText.setText(title);
        final EditText descriptionEditText =
                ((EditText) view.findViewById(R.id.edit_story_header_description_edit_text));
        descriptionEditText.setText(description);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.save_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        sendResult(Activity.RESULT_OK,
                                titleEditText.getText().toString(),
                                descriptionEditText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    private void sendResult(int resultCode, String title, String description) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DESCRIPTION, description);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
