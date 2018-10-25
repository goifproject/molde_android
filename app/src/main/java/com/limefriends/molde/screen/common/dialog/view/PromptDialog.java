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
import com.limefriends.molde.screen.common.controller.BaseObservableDialog;

public class PromptDialog extends BaseObservableDialog<PromptDialog.PromptDialogDismissListener> {

    public interface PromptDialogDismissListener {

        void onPositiveButtonClicked();

        void onNegativeButtonClicked();
    }

    /* package */ public static final String ARG_TITLE = "ARG_TITLE";
    /* package */ public static final String ARG_MESSAGE = "ARG_MESSAGE";
    /* package */ public static final String ARG_POSITIVE_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION";
    /* package */ public static final String ARG_NEGATIVE_BUTTON_CAPTION = "ARG_NEGATIVE_BUTTON_CAPTION";

    private TextView mTxtTitle;
    private TextView mTxtMessage;
    private Button mBtnPositive;
    private Button mBtnNegative;

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
        mTxtTitle = (TextView) rootView.findViewById(R.id.txt_dialog_title);
        mTxtMessage = (TextView) rootView.findViewById(R.id.txt_dialog_message);
        mBtnPositive = (Button) rootView.findViewById(R.id.btn_dialog_positive);
        mBtnNegative = (Button) rootView.findViewById(R.id.btn_dialog_negative);

        mBtnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (PromptDialogDismissListener listener : getListeners()) {
                    listener.onPositiveButtonClicked();
                }
                dismiss();
            }
        });

        mBtnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (PromptDialogDismissListener listener : getListeners()) {
                    listener.onNegativeButtonClicked();
                }
                dismiss();
            }
        });
    }

    private void populateSubViews() {
        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);
        String positiveButtonCaption = getArguments().getString(ARG_POSITIVE_BUTTON_CAPTION);
        String negativeButtonCaption = getArguments().getString(ARG_NEGATIVE_BUTTON_CAPTION);

        mTxtTitle.setText(TextUtils.isEmpty(title) ? "" : title);
        mTxtMessage.setText(TextUtils.isEmpty(message) ? "" : message);
        mBtnPositive.setText(positiveButtonCaption);
        mBtnNegative.setText(negativeButtonCaption);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

}
