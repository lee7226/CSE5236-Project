package project.cse5236.parleypirate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class UpdateDisplayNameDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.action_update_displayname)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(UpdateDisplayNameDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(UpdateDisplayNameDialogFragment.this);
                    }
                }).setView(R.id.action_update_displayname);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface UpdateDisplayNameDialogFragmentListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    UpdateDisplayNameDialogFragment.UpdateDisplayNameDialogFragmentListener mListener;

    // Override the Fragment.onAttach() method to instantiate the DeleteAccountDialogFragmentListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DeleteAccountDialogFragmentListener so we can send events to the host
            mListener = (UpdateDisplayNameDialogFragment.UpdateDisplayNameDialogFragmentListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement UpdateDisplayNameDialogFragmentListener");
        }
    }


}
