package com.example.tp;

import androidx.fragment.app.Fragment;

public interface AddMessage {
    public void setMessageToContainer(String text, Fragment fragment, String fragmentTag, boolean user);
}
