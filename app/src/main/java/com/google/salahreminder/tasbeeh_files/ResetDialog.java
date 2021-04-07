package com.google.salahreminder.tasbeeh_files;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.salahreminder.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ResetDialog extends AppCompatDialogFragment {
    private Context context;
    public ResetDialog(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Reset all values")
                .setIcon(R.drawable.ic_fluent_erase_24_regular)
                .setMessage("This can't be undone. Only target value will remain unchanged.")
                .setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((TasbeehActivity) getActivity()).resetCount(true);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((TasbeehActivity) getActivity()).resetCount(false);
                    }
                });

        return builder.create();
    }
}