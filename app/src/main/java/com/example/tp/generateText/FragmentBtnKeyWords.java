package com.example.tp.generateText;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tp.AddMessage;
import com.example.tp.ControlVisibleEditTextField;
import com.example.tp.FragmentBtnDownloadText;
import com.example.tp.R;
import com.example.tp.SetHeightMessageContainer;
import com.example.tp.*;
import com.example.tp.server.GetAnswerGenerateFromServerTask;
import com.example.tp.server.GetAnswerTranslateFromServerTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class FragmentBtnKeyWords extends ClassWorkingWithNN {
    private Activity mActivity;
    private AddMessage addMessage;
    private ControlVisibleEditTextField controlVisibleEditTextField;
    private SetHeightMessageContainer setHeightMessageContainer;

    public FragmentBtnKeyWords(Activity mActivity) {
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
        View view = inflater.inflate(R.layout.fragment_btns_keywords, container, false);
        view.post(() -> setHeightMessageContainer.setHeightMessageContainer(view.getHeight()));

        init();
        return view;
    }

    private void init() {
        super.onClickSendMsg(mActivity, addMessage, this, "fragmentBtnKeyWords");
        controlVisibleEditTextField.setVisibility(true);
    }

    @Override
    public String requestToServer(String data) throws ExecutionException, InterruptedException {
        Bundle args = this.getArguments();
        String promptText = args.getString("Article");
        promptText += " " + args.getString("Length");
        promptText += " " + data;

        ExecutorService es = Executors.newSingleThreadExecutor();

        Future<String> future = es.submit(new GetAnswerGenerateFromServerTask(promptText));
        es.shutdown();

        return future.get();
    }
}
