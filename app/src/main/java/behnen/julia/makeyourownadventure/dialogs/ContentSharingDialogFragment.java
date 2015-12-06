package behnen.julia.makeyourownadventure.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import behnen.julia.makeyourownadventure.R;

/**
 * A dialog fragment that prompts for content sharing after a story is uploaded.
 *
 * C@author Julia Behnen
 * @version December 6, 2015
 */
public class ContentSharingDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        builder.setMessage(R.string.contentSharingDialogText)
                .setPositiveButton(R.string.share_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendResult(Activity.RESULT_CANCELED);
                    }
                });
        return builder.create();
    }

    /**
     * Sends the dialog result back to the calling activity.
     * @param resultCode The result code from the dialog.
     */
    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
