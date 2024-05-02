package com.example.tp.generateText;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.tp.AddMessage;
import com.example.tp.ControlVisibleEditTextField;
import com.example.tp.MainHandler;
import com.example.tp.R;
import com.example.tp.SetHeightMessageContainer;

public class FragmentBtnChooseArticle extends MainHandler {
    private Activity mActivity;
    private SetHeightMessageContainer setHeightMessageContainer;
    private AddMessage addMessage;
    private ControlVisibleEditTextField controlVisibleEditTextField;

    public FragmentBtnChooseArticle(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addMessage = (AddMessage) context;
        controlVisibleEditTextField = (ControlVisibleEditTextField) context;
        setHeightMessageContainer = (SetHeightMessageContainer) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_btns_choose_article, container, false);
        view.post(() -> setHeightMessageContainer.setHeightMessageContainer(view.getHeight()));

        init(view);
        return view;
    }

    private void init(View view) {
        setToolBar();
        onChooseArticle(view);
        onClickSendMsg();
        controlVisibleEditTextField.setVisibility(true);
    }

    /**
     * Set title and show back button
     */
    private void setToolBar() {
        TextView titlePage = mActivity.findViewById(R.id.titlePage);
        mActivity.findViewById(R.id.backArrowBtn).setVisibility(View.VISIBLE);

        titlePage.setText(getResources().getString(R.string.genTX));

        titlePage.setTextSize(16);
        titlePage.setTextColor(getResources().getColor(R.color.black, mActivity.getTheme()));
    }

    /**
     * Handler of choosing of article
     * @param view
     */
    public void onChooseArticle(View view) {
        AppCompatButton animalBtn = view.findViewById(R.id.animalBtn);
        AppCompatButton peopleBtn = view.findViewById(R.id.peopleBtn);
        AppCompatButton technologyBtn = view.findViewById(R.id.technologyBtn);
        AppCompatButton funHistoryBtn = view.findViewById(R.id.funHistoryBtn);
        AppCompatButton criminalBtn = view.findViewById(R.id.criminalBtn);
        AppCompatButton detectiveBtn = view.findViewById(R.id.detectiveBtn);

        String text2 = getResources().getString(R.string.choose_length_text_msg);
        final String[] text1 = {""};

        animalBtn.setOnClickListener(v -> {
            text1[0] = getResources().getString(R.string.animal_article);
            goToFragmentLength(text2, text1);
        });

        peopleBtn.setOnClickListener(v -> {
            text1[0] = getResources().getString(R.string.people_article);
            goToFragmentLength(text2, text1);
        });

        technologyBtn.setOnClickListener(v -> {
            text1[0] = getResources().getString(R.string.technology_article);
            goToFragmentLength(text2, text1);
        });

        funHistoryBtn.setOnClickListener(v -> {
            text1[0] = getResources().getString(R.string.fun_history);
            goToFragmentLength(text2, text1);
        });

        criminalBtn.setOnClickListener(v -> {
            text1[0] = getResources().getString(R.string.criminal);
            goToFragmentLength(text2, text1);
        });

        detectiveBtn.setOnClickListener(v -> {
            text1[0] = getResources().getString(R.string.detective);
            goToFragmentLength(text2, text1);
        });
    }

    private void goToFragmentLength(String text2, String[] text1) {
        FragmentBtnLengthText fragment = new FragmentBtnLengthText(mActivity);

        Bundle bundle = new Bundle();
        bundle.putString("Article", text1[0]);
        fragment.setArguments(bundle);

        addMessage.setMessageToContainer(text1[0], fragment, "fragmentBtnLengthText", true);
        addMessage.setMessageToContainer(text2, null, "", false);
    }

    private void onClickSendMsg() {
        super.onClickSendMsg(mActivity, addMessage, new FragmentBtnLengthText(mActivity), "fragmentBtnLengthText");
    }
}
