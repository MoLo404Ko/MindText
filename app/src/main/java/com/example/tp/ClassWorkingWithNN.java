package com.example.tp;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.example.tp.interfaces.AddMessage;

import java.util.concurrent.ExecutionException;

public abstract class ClassWorkingWithNN extends MainHandler {
    public abstract String requestToServer(String data) throws ExecutionException, InterruptedException;
    public abstract void controlUiComponents(boolean isVisible);

    /**
     * Handler of clicking on send btn
     * Add message after translated text by NN and go next Fragment
     * @param mActivity - main activity
     * @param addMessage - interface
     */
    protected void onClickSendMsg(Activity mActivity, AddMessage addMessage, Fragment fragment) {
        super.onClickSendMsg(mActivity, addMessage, fragment);
    }

    /**
     * Remove other symbols of html page
     * @param answer - message from server
     * @return - clear answer (without special symbols)
     */
    protected String clearAnswer(String answer) {
        StringBuilder clearAnswer = new StringBuilder();

        char[] charAnswer = answer.toCharArray();
        int charIndex = -1;

        for (Character c: charAnswer) {
            charIndex++;

            // check symbol on the php scripts
            if (c.equals(':')) {
                while (charAnswer[charIndex + 1] != '<') {
                    charIndex++;
                    clearAnswer.append(charAnswer[charIndex]);
                }

                break;
            }
        }

        return clearAnswer.toString();
    }
}
