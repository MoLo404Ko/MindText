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

import com.example.tp.generateText.FragmentBtnChooseArticle;
import com.example.tp.interfaces.AddMessage;
import com.example.tp.interfaces.ControlVisibleEditTextField;
import com.example.tp.interfaces.SetActionBar;
import com.example.tp.interfaces.SetHeightMessageContainer;
import com.example.tp.translateText.FragmentBtnTranslateContainerChooseLanguage;

public class FragmentBtnGeneralContainer extends Fragment {
    private SetHeightMessageContainer setHeightMessageContainer;
    private AddMessage addMessage;
    private SetActionBar setActionBar;
    private ControlVisibleEditTextField controlVisibleEditTextField;
    private final Activity mActivity;

    public FragmentBtnGeneralContainer(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setHeightMessageContainer = (SetHeightMessageContainer) context;
        addMessage = (AddMessage) context;
        controlVisibleEditTextField = (ControlVisibleEditTextField) context;
        setActionBar = (SetActionBar) context;
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
        setActionBar.setActionBar(getString(R.string.main), false);
        controlVisibleEditTextField.setVisibility(false);
    }

    /**
     * Go to generate text
     * @param view - layout
     */
    private void onClickGenerateText(View view) {
        AppCompatButton generateBtn = view.findViewById(R.id.generate_text);
        generateBtn.setOnClickListener(v -> {
            String text1 = getResources().getString(R.string.generate_text_msg);
            String text2 = getResources().getString(R.string.start_generate_msg);

            view.post(() -> {
                addMessage.addMessage(text1, null, "", true);
                addMessage.addMessage(text2, new FragmentBtnChooseArticle(mActivity),
                        "fragmentBtnChooseArticle",false);
            });
        });
    }

    /**
     * Go to translate text
     * @param view - layout
     */
    private void onClickTranslateText(View view) {
        AppCompatButton translateBtn = view.findViewById(R.id.translate_text);

        translateBtn.setOnClickListener(v -> {
            String text1 = getResources().getString(R.string.translate_text);
            String text2 = getResources().getString(R.string.start_translate);

            view.post(() -> {
                addMessage.addMessage(text1,new FragmentBtnTranslateContainerChooseLanguage(mActivity),
                        "fragmentBtnTranslateContainerChooseLanguage",
                        true);
                addMessage.addMessage(text2, null, "",false);
            });
        });
    }
}