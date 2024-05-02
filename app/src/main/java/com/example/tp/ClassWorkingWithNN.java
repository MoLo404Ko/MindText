package com.example.tp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.fragment.app.Fragment;

import java.util.concurrent.ExecutionException;

public abstract class ClassWorkingWithNN extends MainHandler {
    abstract String requestToServerForTranslateScript(String data) throws ExecutionException, InterruptedException;
    abstract void blockingUiComponents(View view);

    /**
     * Handler of clicking on send btn
     * Add message after translated text by NN and go next Fragment
     * @param mActivity
     * @param addMessage
     */
    protected void onClickSendMsg(Activity mActivity, AddMessage addMessage, Fragment fragment, String tag) {
        super.onClickSendMsg(mActivity, addMessage, fragment, tag);
    }

    /**
     * Remove other symbols of html page
     * @param answer
     * @return
     */
    protected String clearAnswer(String answer) {
        StringBuilder clearAnswer = new StringBuilder();
        char[] charAnswer = answer.toCharArray();
        int charIndex = -1;

        for (Character c: charAnswer) {
            charIndex++;

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
