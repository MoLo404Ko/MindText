package com.example.tp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tp.generateText.FragmentBtnChooseArticle;

public class FragmentBtnGeneralContainer extends Fragment {
    private SetHeightMessageContainer setHeightMessageContainer;
    private AddMessage addMessage;
    private ControlVisibleEditTextField controlVisibleEditTextField;
    private Activity mActivity;

    public FragmentBtnGeneralContainer(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setHeightMessageContainer = (SetHeightMessageContainer) context;
        addMessage = (AddMessage) context;
        controlVisibleEditTextField = (ControlVisibleEditTextField) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_btns_general_container, container, false);
        view.post(() -> setHeightMessageContainer.setHeightMessageContainer(view.getHeight()));

        init(view);

        onClickGenerateText(view);
        onClickTranslateText(view);


        return view;
    }

    private void init(View view) {
        onClickGenerateText(view);
        setToolBar();
        controlVisibleEditTextField.setVisibility(false);
    }

    /**
     * Set title and show back button
     */
    private void setToolBar() {
        TextView titlePage = (TextView)mActivity.findViewById(R.id.titlePage);
        mActivity.findViewById(R.id.backArrowBtn).setVisibility(View.GONE);

        titlePage.setText(getResources().getString(R.string.main));

        titlePage.setTextSize(16);
        titlePage.setTextColor(getResources().getColor(R.color.black, mActivity.getTheme()));
    }

    /**
     * Обработка нажатия клика на кнопку "генерация текста"
     * @param view
     */
    private void onClickGenerateText(View view) {
        AppCompatButton generateBtn = view.findViewById(R.id.generate_text);
        generateBtn.setOnClickListener(v -> {
            String text1 = getResources().getString(R.string.generate_text_msg);
            String text2 = getResources().getString(R.string.start_generate_msg);

            view.post(() -> {
                addMessage.setMessageToContainer(text1, null, "", true);
                addMessage.setMessageToContainer(text2, new FragmentBtnChooseArticle(mActivity),
                        "fragmentBtnChooseArticle",false);
            });
        });
    }

    /**
     * Обработка нажатия клика на кнопку "перевод текста"
     * @param view
     */
    private void onClickTranslateText(View view) {
        AppCompatButton translateBtn = view.findViewById(R.id.translate_text);

        translateBtn.setOnClickListener(v -> {
            String text1 = getResources().getString(R.string.translate_text);
            String text2 = getResources().getString(R.string.start_translate);

            view.post(() -> {
                addMessage.setMessageToContainer(text1,new FragmentBtnTranslateContainerChooseLanguage(mActivity),
                        "fragmentBtnTranslateContainerChooseLanguage",
                        true);
                addMessage.setMessageToContainer(text2, null, "",false);
            });
        });
    }
}