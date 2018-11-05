package project.cse5236.parleypirate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class UpdateDisplayNameDialogFragment extends DialogFragment {

    private EditText mEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = inflater.inflate(R.layout.dialog_update_displayname,null);
        mEditText = v.findViewById(R.id.update_display_name_edittext);
        builder.setMessage(R.string.action_update_displayname)
                .setPositiveButton(R.string.change, (dialog, id) ->
                        mListener.onUpdateDisplayNameDialogFragmentPositiveClick(UpdateDisplayNameDialogFragment.this,String.valueOf(mEditText.getText())))
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                    //do nothing
                }).setView(v);
        return builder.create();
    }

    public interface UpdateDisplayNameDialogFragmentListener {
        void onUpdateDisplayNameDialogFragmentPositiveClick(DialogFragment dialog, String result);
    }

    // Use this instance of the interface to deliver action events
    UpdateDisplayNameDialogFragment.UpdateDisplayNameDialogFragmentListener mListener;

    // Override the Fragment.onAttach() method to instantiate the DeleteAccountDialogFragmentListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the UpdateDisplayNameDialogFragmentListener so we can send events to the host
            mListener = (UpdateDisplayNameDialogFragment.UpdateDisplayNameDialogFragmentListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement UpdateDisplayNameDialogFragmentListener");
        }
    }


}
