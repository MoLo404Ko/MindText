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
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.tp.AddMessage;
import com.example.tp.ControlVisibleEditTextField;
import com.example.tp.R;
import com.example.tp.SetHeightMessageContainer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FragmentBtnLengthText extends Fragment {
    private SetHeightMessageContainer setHeightMessageContainer;
    private AddMessage addMessage;
    private ControlVisibleEditTextField controlVisibleEditTextField;
    private final Activity mActivity;

    public FragmentBtnLengthText(Activity mActivity) {
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
        View view = inflater.inflate(R.layout.fragment_btns_lenght_text, container, false);
        view.post(() -> setHeightMessageContainer.setHeightMessageContainer(view.getHeight()));

        init(view);

        return view;
    }

    private void init(View view) {
        onChooseLength(view);
        controlVisibleEditTextField.setVisibility(false);
    }

    private void onChooseLength(View view) {
        AppCompatButton lessTen = view.findViewById(R.id.lessTen);
        AppCompatButton lessTwentyFive = view.findViewById(R.id.lessTwentyFive);
        AppCompatButton lessFifty = view.findViewById(R.id.lessFifty);

        lessTen.setOnClickListener(v -> {
            String length = getResources().getString(R.string.less_10);
            goToFragmentKeyWords(length);
        });
        lessTwentyFive.setOnClickListener(v -> {
            String length = getResources().getString(R.string.less_25);
            goToFragmentKeyWords(length);
        });
        lessFifty.setOnClickListener(v -> {
            String length = getResources().getString(R.string.less_50);
            goToFragmentKeyWords(length);
        });
    }

    private void goToFragmentKeyWords(String length) {
        FragmentBtnKeyWords fragment = new FragmentBtnKeyWords(mActivity);

        Bundle bundle = new Bundle();
        bundle.putString("Article", this.getArguments().getString("Article"));
        bundle.putString("Length", length);
        fragment.setArguments(bundle);

        addMessage.setMessageToContainer(length, fragment,
                "fragmentBtnKeyWords", true);
        addMessage.setMessageToContainer(getResources().getString(R.string.key_words_msg),
                null, "", false);
    }
}
