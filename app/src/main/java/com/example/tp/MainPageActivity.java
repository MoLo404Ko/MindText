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

import com.example.tp.generateText.FragmentBtnChooseArticle;
import com.example.tp.generateText.FragmentBtnLengthText;
import com.example.tp.translateText.FragmentBtnTranslateContainerChooseLanguage;

import java.util.Set;

public class MainPageActivity extends FragmentActivity implements SetHeightMessageContainer, AddMessage,
        ControlVisibleEditTextField {
    private FragmentMessageContainer fragmentMessageContainer = null;

    /**
     * Динамическое определение высоты контйенера сообщений
     * @param heightBtnContainer - высота контейнера с кнопками
     */
    @Override
    public void setHeightMessageContainer(int heightBtnContainer) {
        int heightMessageContainer = 0;
        int heightBottomLayout = dpToPx(findViewById(R.id.bottom_layout).getHeight());

        // Высчитываем размер контейнера (размер экрана - панель кнопок - панель с EditText)
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightMessageContainer = displayMetrics.heightPixels - heightBtnContainer - heightBottomLayout - dpToPx(50); // 134 - margins

        if (fragmentMessageContainer == null) {
            // Создаем контейнер для сообщений
            Bundle args = new Bundle();
            args.putInt("height", heightMessageContainer);

            fragmentMessageContainer = new FragmentMessageContainer();
            fragmentMessageContainer.setArguments(args);

            getSupportFragmentManager().beginTransaction().add(R.id.message_container, fragmentMessageContainer).commit();
        }
        else {
            fragmentMessageContainer.setHeightMessageContainer(heightMessageContainer);
        }
    }

    /**
     *
     * @param text
     * @param fragment
     * @param user
     */
    @Override
    public void setMessageToContainer(String text, Fragment fragment, String fragmentTag, boolean user) {
        if (!text.isEmpty())
            fragmentMessageContainer.addMessage(text, user);

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
     * Устанавливаем фрагмент с кнопками
     */
    public void setFragment() {
        FragmentBtnGeneralContainer fragmentBtnGeneralContainer = new FragmentBtnGeneralContainer(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.btn_container, fragmentBtnGeneralContainer, "fragmentBtnGeneralContainer").commit();
    }

    /**
     * Перевод dp в px
     * @param dp
     * @return
     */
    public int dpToPx(int dp) {
        return (int)(dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public void goBack() {
        FragmentManager fm = getSupportFragmentManager();
        AppCompatImageButton backBtn = findViewById(R.id.backArrowBtn);

        backBtn.setOnClickListener(view -> {
            String fragmentTag = fm.getFragments().get(1).getTag();
            if (!fragmentTag.isEmpty()) {

                Set<Thread> set = Thread.getAllStackTraces().keySet();
                for (Thread t: set) {
                    if (t.getName().equals("translateTextThread") || t.getName().equals("generateTextThread")) {
                        t.interrupt();
                        break;
                    }
                }

                switch (fragmentTag) {
                    case "fragmentBtnTextForTranslateContainer": {
                        getSupportFragmentManager().beginTransaction().replace(R.id.btn_container,
                                new FragmentBtnTranslateContainerChooseLanguage(this), "fragmentBtnTranslateContainerChooseLanguage").commit();
                        setMessageToContainer("назад", null,
                                "fragmentBtnTranslateContainerChooseLanguage",true);
                        break;
                    }
                    case "fragmentBtnTranslateContainerChooseLanguage":
                    case "fragmentBtnChooseArticle":
                    case "fragmentBtnDownloadText": {
                        getSupportFragmentManager().beginTransaction().replace(R.id.btn_container,
                                new FragmentBtnGeneralContainer(this), "fragmentBtnGeneralContainer").commit();
                        setMessageToContainer("назад", null,
                                "fragmentBtnGeneralContainer",true);
                        break;
                    }
                    case "fragmentBtnLengthText": {
                        getSupportFragmentManager().beginTransaction().replace(R.id.btn_container,
                                new FragmentBtnChooseArticle(this), "fragmentBtnChooseArticle").commit();
                        setMessageToContainer("назад", null,
                                "fragmentBtnChooseArticle",true);
                        break;
                    }
                    case "fragmentBtnKeyWords": {
                        getSupportFragmentManager().beginTransaction().replace(R.id.btn_container,
                                new FragmentBtnLengthText(this), "fragmentBtnLengthText").commit();
                        setMessageToContainer("назад", null,
                                "fragmentBtnLengthText",true);
                        break;
                    }
                }
            }
        });
    }
}

