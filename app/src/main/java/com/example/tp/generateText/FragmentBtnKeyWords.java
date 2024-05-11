package com.example.tp.generateText;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tp.interfaces.AddMessage;
import com.example.tp.interfaces.ControlVisibleEditTextField;
import com.example.tp.R;
import com.example.tp.interfaces.SetHeightMessageContainer;
import com.example.tp.*;
import com.example.tp.server.GetAnswerGenerateFromServerTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class FragmentBtnKeyWords extends ClassWorkingWithNN {
    private final Activity mActivity;
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
        super.onClickSendMsg(mActivity, addMessage, this);
        controlVisibleEditTextField.setVisibility(true);
    }

    /**
     * Request to server for generate text
     * @param keyWords - key words
     * @return text from server
     */
    @Override
    public String requestToServer(String keyWords) throws InterruptedException {
        Bundle args = this.getArguments();

        assert args != null;
        String promptText = args.getString("Article");
        promptText += " " + args.getString("Length");
        promptText += " " + keyWords;

        ExecutorService es = Executors.newSingleThreadExecutor();

        Future<String> future = es.submit(new GetAnswerGenerateFromServerTask(promptText));
        es.shutdown();

        try {
            return future.get();
        } catch (ExecutionException e) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> Toast.makeText(mActivity,
                    getString(R.string.cant_connect), Toast.LENGTH_SHORT).show());
        }
        return "";
    }

    @Override
    public void controlUiComponents(boolean isVisible) {
        controlVisibleEditTextField.setVisibility(isVisible);
    }
}
