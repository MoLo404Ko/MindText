package com.example.tp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.example.tp.server.GetAnswerFromServerTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FragmentBtnTranslateContainerChooseLanguage extends Fragment {
    private SetHeightMessageContainer setHeightMessageContainer;
    private AddMessage addMessage;
    private ControlVisibleEditTextField controlVisibleEditTextField;
    private final Activity mActivity;


    public FragmentBtnTranslateContainerChooseLanguage(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addMessage = (AddMessage) context;
        setHeightMessageContainer = (SetHeightMessageContainer) context;
        controlVisibleEditTextField = (ControlVisibleEditTextField) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_btns_translate_container_choose_language, container, false);
        view.post(() -> setHeightMessageContainer.setHeightMessageContainer(view.getHeight()));

        init(view);
        return view;
    }

    private void init(View view) {
        onChooseLanguage(view);
        setToolBar();
        controlVisibleEditTextField.setVisibility(false);
    }

    /**
     * Set title and show back button
     */
    private void setToolBar() {
        TextView titlePage = mActivity.findViewById(R.id.titlePage);
        mActivity.findViewById(R.id.backArrowBtn).setVisibility(View.VISIBLE);

        titlePage.setText(getResources().getString(R.string.transTX));

        titlePage.setTextSize(16);
        titlePage.setTextColor(getResources().getColor(R.color.black, mActivity.getTheme()));
    }

    /**
     * Choose language buttons
     */
    private void onChooseLanguage(View view) {
        AppCompatButton russianLanguage = view.findViewById(R.id.russian_language);
        AppCompatButton englishLanguage = view.findViewById(R.id.english_language);

        String text2 = getResources().getString(R.string.chosen_language);

        russianLanguage.setOnClickListener(view1 -> view.post(() -> {
            String text1 = getResources().getString(R.string.animal_article);
            addMessage.setMessageToContainer(text1, new FragmentBtnTextForTranslateContainer(mActivity),
                    "fragmentBtnTextForTranslateContainer", true);
            addMessage.setMessageToContainer(text2, null, "",false);
        }));


        englishLanguage.setOnClickListener(view1 -> {
            String text1 = getResources().getString(R.string.english_language);
            addMessage.setMessageToContainer(text1, new FragmentBtnTextForTranslateContainer(mActivity),
                    "fragmentBtnTextForTranslateContainer", true);
            addMessage.setMessageToContainer(text2, null, "",false);
        });
    }
}
