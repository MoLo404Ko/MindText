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

import com.example.tp.interfaces.AddMessage;
import com.example.tp.interfaces.ControlVisibleEditTextField;
import com.example.tp.MainHandler;
import com.example.tp.R;
import com.example.tp.interfaces.SetActionBar;
import com.example.tp.interfaces.SetHeightMessageContainer;

public class FragmentBtnChooseArticle extends MainHandler {
    private final Activity mActivity;
    private SetHeightMessageContainer setHeightMessageContainer;
    private AddMessage addMessage;
    private ControlVisibleEditTextField controlVisibleEditTextField;
    private SetActionBar setActionBar;

    public FragmentBtnChooseArticle(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addMessage = (AddMessage) context;
        controlVisibleEditTextField = (ControlVisibleEditTextField) context;
        setHeightMessageContainer = (SetHeightMessageContainer) context;
        setActionBar = (SetActionBar) context;
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
        setActionBar.setActionBar(getString(R.string.genTX), true);
        onChooseArticle(view);
        onClickSendMsg();
        controlVisibleEditTextField.setVisibility(true);
    }

    /**
     * Handler of choosing of article
     * @param view - layout
     */
    public void onChooseArticle(View view) {
        AppCompatButton animalBtn = view.findViewById(R.id.animalBtn);
        AppCompatButton peopleBtn = view.findViewById(R.id.peopleBtn);
        AppCompatButton technologyBtn = view.findViewById(R.id.technologyBtn);
        AppCompatButton funHistoryBtn = view.findViewById(R.id.funHistoryBtn);
        AppCompatButton criminalBtn = view.findViewById(R.id.criminalBtn);
        AppCompatButton detectiveBtn = view.findViewById(R.id.detectiveBtn);

        animalBtn.setOnClickListener(v -> goToFragmentLength(getResources().getString(R.string.animal_article)));
        peopleBtn.setOnClickListener(v -> goToFragmentLength(getResources().getString(R.string.people_article)));
        technologyBtn.setOnClickListener(v -> goToFragmentLength(getResources().getString(R.string.technology_article)));
        funHistoryBtn.setOnClickListener(v -> goToFragmentLength(getResources().getString(R.string.fun_history)));
        criminalBtn.setOnClickListener(v -> goToFragmentLength(getResources().getString(R.string.criminal)));
        detectiveBtn.setOnClickListener(v -> goToFragmentLength(getResources().getString(R.string.detective)));
    }

    /**
     * Goes to the next fragment and sets the arguments
     * @param article - theme of the text to generate
     */
    private void goToFragmentLength(String article) {
        FragmentBtnLengthText fragment = new FragmentBtnLengthText(mActivity);

        Bundle bundle = new Bundle();
        bundle.putString("Article", article);
        fragment.setArguments(bundle);

        addMessage.addMessage(article, fragment, "fragmentBtnLengthText", true);
        addMessage.addMessage(getString(R.string.choose_article), null, "", false);
    }

    private void onClickSendMsg() {
        super.onClickSendMsg(mActivity, addMessage, this);
    }
}
