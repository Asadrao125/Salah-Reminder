package com.google.salahreminder.tasbeeh_files;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.google.salahreminder.R;
import com.google.salahreminder.activities.TasbeehActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import static com.google.salahreminder.BuildConfig.VERSION_NAME;

public class AboutDialog extends AppCompatDialogFragment {

    private Context context;

    public AboutDialog(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("About")
                .setIcon(R.drawable.ic_fluent_info_24_regular)
                .setMessage("Specifically Designed for Ramadan\n\n" + "Version " + VERSION_NAME)
                .setPositiveButton("CLOSE", null);
                /*.setNeutralButton("SOCMED", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((TasbeehActivity) getActivity()).openWebPage("https://linktr.ee/iqFareez");
                    }
                });*/
        return builder.create();
    }
}