package project.cse5236.parleypirate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class DeleteAccountDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_account_confirmation)
                .setPositiveButton(android.R.string.yes, (dialog, id) -> mListener.onDeleteAccountDialogFragmentPositiveClick(DeleteAccountDialogFragment.this))
                .setNegativeButton(android.R.string.no, (dialog, id) -> {
                    //do nothing
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface DeleteAccountDialogFragmentListener {
        void onDeleteAccountDialogFragmentPositiveClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    DeleteAccountDialogFragmentListener mListener;

    // Override the Fragment.onAttach() method to instantiate the DeleteAccountDialogFragmentListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DeleteAccountDialogFragmentListener so we can send events to the host
            mListener = (DeleteAccountDialogFragmentListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement DeleteAccountDialogFragmentListener");
        }
    }


}
