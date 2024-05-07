package com.example.tp;


import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tp.findObjectsText.FragmentBtnObjects;
import com.example.tp.generateText.FragmentBtnChooseArticle;
import com.example.tp.generateText.FragmentBtnLengthText;
import com.example.tp.interfaces.AddMessage;
import com.example.tp.interfaces.ControlVisibleEditTextField;
import com.example.tp.interfaces.SetHeightMessageContainer;
import com.example.tp.interfaces.SetActionBar;
import com.example.tp.translateText.FragmentBtnTranslateContainerChooseLanguage;

import java.util.Set;

public class MainPageActivity extends FragmentActivity implements SetHeightMessageContainer, AddMessage,
        ControlVisibleEditTextField, SetActionBar {
    private FragmentMessageContainer fragmentMessageContainer = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        init();
    }

    private void init() {
        setFragment();
        goBack();
//        getToken();
    }

    /**
     * Динамическое определение высоты контйенера сообщений
     * @param heightBtnContainer - высота контейнера с кнопками
     */
    @Override
    public void setHeightMessageContainer(int heightBtnContainer) {
        int heightMessageContainer = 0;
        int heightBottomLayout = dpToPx(findViewById(R.id.bottom_layout).getHeight());

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightMessageContainer = displayMetrics.heightPixels - heightBtnContainer - heightBottomLayout - dpToPx(50); // 50 - margins

        /**
         * For first message create block of messages
         */
        if (fragmentMessageContainer == null) {
            Bundle args = new Bundle();
            args.putInt("height", heightMessageContainer);

            fragmentMessageContainer = new FragmentMessageContainer();
            fragmentMessageContainer.setArguments(args);

            getSupportFragmentManager().beginTransaction().add(R.id.message_container, fragmentMessageContainer).commit();
        }
        else fragmentMessageContainer.setHeightMessageContainer(heightMessageContainer);
    }


    /**
     * Set message to message container
     * @param text - Text to add
     * @param fragment - current fragment
     * @param user - isUser
     */
    @Override
    public void addMessage(String text, Fragment fragment, String fragmentTag, boolean user) {
        // add message
        if (!text.isEmpty())
            fragmentMessageContainer.addMessage(text, user);

        // redraw block of messages
        if (fragment != null) {
            ConstraintLayout constraintLayout = findViewById(R.id.root_layout);
            ConstraintSet set = new ConstraintSet();

            set.clone(constraintLayout);
            set.connect(R.id.btn_container, ConstraintSet.BOTTOM, R.id.bottom_layout, ConstraintSet.TOP);
            set.applyTo(constraintLayout);

            getSupportFragmentManager().beginTransaction().replace(R.id.btn_container, fragment, fragmentTag).commit();
        }
    }

    /**
     * Set visible for EditText
     * @param isVisible
     */
     public void setVisibility(boolean isVisible) {
        EditText inputField = findViewById(R.id.inputField);
        AppCompatImageButton sendBtn = findViewById(R.id.sendMessage);

        inputField.setText("");

        if (isVisible) {
            inputField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_input_field, getTheme()));
            inputField.setEnabled(true);

            sendBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_send, getTheme()));
            sendBtn.setEnabled(true);
        }
        else {
            inputField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_input_field_blocked, getTheme()));
            inputField.setEnabled(false);

            sendBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_send_blocked, getTheme()));
            sendBtn.setEnabled(false);
        }
    }


    /**
     * Set start fragment
     */
    public void setFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.btn_container,
                new FragmentBtnGeneralContainer(this), "fragmentBtnGeneralContainer").commit();
    }

    /**
     * Transfet dp to px
     * @param dp
     * @return size in px
     */
    public int dpToPx(int dp) {
        return (int)(dp * Resources.getSystem().getDisplayMetrics().density);
    }


    /**
     * The method of processing the transition back
     */
    public void goBack() {
        FragmentManager fm = getSupportFragmentManager();
        AppCompatImageButton backBtn = findViewById(R.id.backArrowBtn);

        backBtn.setOnClickListener(view -> {
            FragmentTransaction ft = fm.beginTransaction();
            String fragmentTag = fm.getFragments().get(1).getTag();

            assert fragmentTag != null;
            if (!fragmentTag.isEmpty()) {
                if (checkThread()) addMessage(getString(R.string.cancel_request), null, "", false);

                Fragment currentFragment = fm.getFragments().get(1);
                Fragment fragment = null;
                String tag = "";

                switch (fragmentTag) {
                    case "fragmentBtnTextForTranslateContainer": {
                        fragment = new FragmentBtnTranslateContainerChooseLanguage(this);
                        tag = "fragmentBtnTranslateContainerChooseLanguage";
                        break;
                    }
                    case "fragmentBtnObjects":
                    case "fragmentBtnDoneText":
                    case "fragmentBtnTonText":
                    case "fragmentBtnTranslateContainerChooseLanguage":
                    case "fragmentBtnChooseArticle":
                    case "fragmentBtnDownloadText": {
                        fragment = new FragmentBtnGeneralContainer(this);
                        tag = "fragmentBtnGeneralContainer";
                        break;
                    }
                    case "fragmentBtnLengthText": {
                        fragment = new FragmentBtnChooseArticle(this);
                        tag = "fragmentBtnChooseArticle";
                        break;
                    }
                    case "fragmentBtnKeyWords": {
                        Bundle args = new Bundle();

                        // save article
                        fragment = new FragmentBtnLengthText(this);
                        args.putString("Article", currentFragment.getArguments().getString("Article"));
                        fragment.setArguments(args);

                        tag = "fragmentBtnLengthText";
                        break;
                    }
                    case "fragmentBtnFindObjectsText": {
                        fragment = new FragmentBtnObjects(this);
                        tag = "fragmentBtnObjects";

                        break;
                    }

                }

                if (fragment != null) {
                    ft.replace(R.id.btn_container, fragment, tag).commit();
                    addMessage("назад", null,
                            "fragmentBtnTranslateContainerChooseLanguage",true);
                }
            }
        });
    }

    /**
     * Check the streams, if such exist, then we interrupt them when going back
     * return - thread if found
     */
    private boolean checkThread() {
        Set<Thread> set = Thread.getAllStackTraces().keySet();
        for (Thread t: set) {
            String threadName = t.getName();
            if (threadName.equals("translateTextThread") || threadName.equals("generateTextThread")
                    || threadName.equals("tonThread") || threadName.equals("findObjectsThread")) {
                t.interrupt();
                return true;
            }
        }

        return false;
    }

    /**
     * Set action bar
     * @param title - name of page
     */
    @Override
    public void setActionBar(String title, boolean backArrowIsVisible) {
        TextView titlePage = this.findViewById(R.id.titlePage);
        AppCompatImageButton backBtn = this.findViewById(R.id.backArrowBtn);

        if (backArrowIsVisible) backBtn.setVisibility(View.VISIBLE);
        else backBtn.setVisibility(View.GONE);

        titlePage.setText(title);

        titlePage.setTextSize(16);
        titlePage.setTextColor(getResources().getColor(R.color.black, this.getTheme()));
    }
}

