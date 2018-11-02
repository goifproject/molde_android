package com.limefriends.molde.screen.common.dialog;

import android.os.Bundle;

import com.limefriends.molde.screen.common.dialog.view.ImageDialog;
import com.limefriends.molde.screen.common.dialog.view.InfoDialog;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;

public class DialogFactory {


    /**
     * Get a new instance of {@link InfoDialog}.
     */
    public InfoDialog newInfoDialog(String title, String message, String buttonCaption) {
        Bundle args = new Bundle(3);
        args.putString(InfoDialog.ARG_TITLE, title);
        args.putString(InfoDialog.ARG_MESSAGE, message);
        args.putString(InfoDialog.ARG_BUTTON_CAPTION, buttonCaption);

        InfoDialog infoDialog = new InfoDialog();
        infoDialog.setArguments(args);

        return infoDialog;
    }


    /**
     * Get a new instance of {@link PromptDialog}.
     */
    public PromptDialog newPromptDialog(String title, String message, String positiveButtonCaption,
                                        String negativeButtonCaption) {
        Bundle args = new Bundle(4);
        args.putString(PromptDialog.ARG_TITLE, title);
        args.putString(PromptDialog.ARG_MESSAGE, message);
        args.putString(PromptDialog.ARG_POSITIVE_BUTTON_CAPTION, positiveButtonCaption);
        args.putString(PromptDialog.ARG_NEGATIVE_BUTTON_CAPTION, negativeButtonCaption);

        PromptDialog promptDialog = new PromptDialog();
        promptDialog.setArguments(args);

        return promptDialog;
    }


    /**
     * Get a new instance of {@link ImageDialog}.
     */
    public ImageDialog newImageDialog(String imageUrl) {
        Bundle args = new Bundle(1);
        args.putString(ImageDialog.ARG_IMAGE_URL, imageUrl);

        ImageDialog imageDialog = new ImageDialog();
        imageDialog.setArguments(args);

        return imageDialog;
    }

}
