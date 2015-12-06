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
 * A dialog fragment that allows story headers to be edited.
 *
 * C@author Julia Behnen
 * @version December 6, 2015
 */
public class EditStoryHeaderDialogFragment extends DialogFragment {

    /**
     * The tag used to identify the title string extra in the intent.
     */
    public static final String EXTRA_TITLE = "behnen.julia.makeyourownadventrue.title";
    /**
     * The tag used to identify the description string extra in the intent.
     */
    public static final String EXTRA_DESCRIPTION = "behnen.julia.makeyourownadventrue.description";

    /**
     * The tag used to identify the title argument in the bundle.
     */
    private static final String ARG_TITLE = "title";
    /**
     * The tag used to identify the description argument in the bundle.
     */
    private static final String ARG_DESCRIPTION = "description";

    /**
     * Creates a new instance of EditStoryHeaderDialogFragment.
     * @param title The title that will be displayed and made available for editing.
     * @param description The description that will be displayed and made available for editing.
     * @return A new instance of EditStoryHeaderDialogFragment that displays the given
     * title and description.
     */
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

        // Get data from bundle
        String title = getArguments().getString(ARG_TITLE);
        String description = getArguments().getString(ARG_DESCRIPTION);

        // Get views from layout and populate them with the data
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

    /**
     * Sends the dialog result back to the calling activity.
     * @param resultCode The result code from the dialog.
     * @param title The title entered in the dialog.
     * @param description The description entered in the dialog
     */
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
