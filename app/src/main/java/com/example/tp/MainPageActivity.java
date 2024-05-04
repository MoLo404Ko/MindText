package com.example.tp;


import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tp.generateText.FragmentBtnChooseArticle;
import com.example.tp.generateText.FragmentBtnLengthText;
import com.example.tp.translateText.FragmentBtnTranslateContainerChooseLanguage;

import java.util.Set;

public class MainPageActivity extends FragmentActivity implements SetHeightMessageContainer, AddMessage,
        ControlVisibleEditTextField {
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
     * @param text
     * @param fragment
     * @param user
     */
    @Override
    public void setMessageToContainer(String text, Fragment fragment, String fragmentTag, boolean user) {
        // add message
        if (!text.isEmpty())
            fragmentMessageContainer.addMessage(text, user);

        // redraw message container
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
        inputField.setText("");

        if (isVisible) {
            inputField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_input_field, getTheme()));
            inputField.setEnabled(true);
        }
        else {
            inputField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_input_field_blocked, getTheme()));
            inputField.setEnabled(false);
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

            if (!fragmentTag.isEmpty()) {
                checkThread();

                Fragment fragment = null;
                String tag = "";

                switch (fragmentTag) {
                    case "fragmentBtnTextForTranslateContainer": {
                        fragment = new FragmentBtnTranslateContainerChooseLanguage(this);
                        tag = "fragmentBtnTranslateContainerChooseLanguage";
                        break;
                    }
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
                        fragment = new FragmentBtnLengthText(this);
                        tag = "fragmentBtnLengthText";
                        break;
                    }

                }

                if (fragment != null) {
                    ft.replace(R.id.btn_container, fragment, tag).commit();
                    setMessageToContainer("назад", null,
                            "fragmentBtnTranslateContainerChooseLanguage",true);
                }
            }
        });
    }

    /**
     * Check the streams, if such exist, then we interrupt them when going back
     */
    private void checkThread() {
        Set<Thread> set = Thread.getAllStackTraces().keySet();
        for (Thread t: set) {
            if (t.getName().equals("translateTextThread") || t.getName().equals("generateTextThread")) {
                t.interrupt();
                break;
            }
        }
    }
}

