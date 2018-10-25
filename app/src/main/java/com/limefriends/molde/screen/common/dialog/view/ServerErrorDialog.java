package com.limefriends.molde.screen.common.dialog.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ServerErrorDialog extends DialogFragment {


    public static ServerErrorDialog newInstance() {
        return new ServerErrorDialog();
    }

    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        return alertDialogBuilder.create();

    }
}
