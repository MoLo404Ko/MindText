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

import com.example.tp.findObjectsText.FragmentBtnFindObjectsText;
import com.example.tp.findObjectsText.FragmentBtnObjects;
import com.example.tp.generateText.FragmentBtnChooseArticle;
import com.example.tp.interfaces.AddMessage;
import com.example.tp.interfaces.ControlVisibleEditTextField;
import com.example.tp.interfaces.SetActionBar;
import com.example.tp.interfaces.SetHeightMessageContainer;
import com.example.tp.tonText.FragmentBtnTonText;
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

        return view;
    }

    private void init(View view) {
        onClickTranslateText(view);
        onClickGenerateText(view);
        onClickTonText(view);
        onClickFindObjectsText(view);
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
            view.post(() -> {
                addMessage.addMessage(getResources().getString(R.string.generate_text_msg),
                        null, "", true);
                addMessage.addMessage(getResources().getString(R.string.start_generate_msg),
                        new FragmentBtnChooseArticle(mActivity),
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
            view.post(() -> {
                addMessage.addMessage(getResources().getString(R.string.translate_text),
                        new FragmentBtnTranslateContainerChooseLanguage(mActivity),
                        "fragmentBtnTranslateContainerChooseLanguage", true);
                addMessage.addMessage(getResources().getString(R.string.start_translate),
                        null, "",false);
            });
        });
    }

    /**
     * Go to identify tonality of text
     * @param view - layout
     */
    private void onClickTonText(View view) {
        AppCompatButton tonBtn = view.findViewById(R.id.ton_text);
        tonBtn.setOnClickListener(v -> {
            view.post(() -> {
                addMessage.addMessage(getResources().getString(R.string.ton_text),
                        new FragmentBtnTonText(mActivity),
                        "fragmentBtnTonText", true);
                addMessage.addMessage(getResources().getString(R.string.start_ton),
                        null, "",false);
            });
        });
    }

    /**
     * Go to find objects
     * @param view - layout
     */
    private void onClickFindObjectsText(View view) {
        AppCompatButton findObjectsBtn = view.findViewById(R.id.find_objects);
        findObjectsBtn.setOnClickListener(v -> {
            addMessage.addMessage(getResources().getString(R.string.find_objects_text),
                    new FragmentBtnObjects(mActivity), "fragmentBtnObjects", true);
            addMessage.addMessage(getResources().getString(R.string.start_objects_find),
                    null, "", false);
        });
    }
}