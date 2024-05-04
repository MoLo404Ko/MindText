package com.example.tp.interfaces;

import androidx.fragment.app.Fragment;

public interface AddMessage {
    /**
     * add message to message container
     * @param text - message
     * @param fragment - current fragment
     * @param fragmentTag - tag current fragment
     * @param user - show from user side or bot side
     */
    void addMessage(String text, Fragment fragment, String fragmentTag, boolean user);
}
