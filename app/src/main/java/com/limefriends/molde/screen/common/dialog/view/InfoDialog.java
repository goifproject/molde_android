package com.limefriends.molde.screen.common.dialog.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.controller.BaseDialog;

public class InfoDialog extends BaseDialog {

    /* package */ public static final String ARG_TITLE = "ARG_TITLE";
    /* package */ public static final String ARG_MESSAGE = "ARG_MESSAGE";
    /* package */ public static final String ARG_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION";

    private TextView mTxtTitle;
    private TextView mTxtMessage;
    private Button mBtnPositive;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getInjector().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_info_prompt, null);
        dialogBuilder.setView(dialogView);

        initSubViews(dialogView);

        populateSubViews();

        setCancelable(true);

        return dialogBuilder.create();
    }

    private void initSubViews(View rootView) {
        mTxtTitle = rootView.findViewById(R.id.txt_dialog_title);
        mTxtMessage = rootView.findViewById(R.id.txt_dialog_message);
        mBtnPositive = rootView.findViewById(R.id.btn_dialog_positive);

        // Hide "negative" button - it is used only in PromptDialog
        rootView.findViewById(R.id.btn_dialog_negative).setVisibility(View.GONE);

        mBtnPositive.setOnClickListener(v -> dismiss());
    }

    private void populateSubViews() {
        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);
        String positiveButtonCaption = getArguments().getString(ARG_BUTTON_CAPTION);

        mTxtTitle.setText(TextUtils.isEmpty(title) ? "" : title);
        mTxtMessage.setText(TextUtils.isEmpty(message) ? "" : message);
        mBtnPositive.setText(positiveButtonCaption);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        // mEventBus.post(new InfoDialogDismissedEvent(getDialogTag()));
    }

}
