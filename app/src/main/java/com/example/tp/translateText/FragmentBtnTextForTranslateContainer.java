package com.example.tp.translateText;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.res.ResourcesCompat;

import com.example.tp.AddMessage;
import com.example.tp.ClassWorkingWithNN;
import com.example.tp.Constants;
import com.example.tp.ControlVisibleEditTextField;
import com.example.tp.FragmentBtnDownloadText;
import com.example.tp.R;
import com.example.tp.SetHeightMessageContainer;
import com.example.tp.server.GetAnswerTranslateFromServerTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FragmentBtnTextForTranslateContainer extends ClassWorkingWithNN {
    private AddMessage addMessage;
    private SetHeightMessageContainer setHeightMessageContainer;
    private ControlVisibleEditTextField controlVisibleEditTextField;
    private Activity mActivity;

    public FragmentBtnTextForTranslateContainer(Activity mActivity) {
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
        View view = inflater.inflate(R.layout.fragment_btns_text_for_translate_container, container, false);
        view.post(() -> setHeightMessageContainer.setHeightMessageContainer(view.getHeight()));

        init(view);
        return view;
    }

    private void init(View view) {
        controlVisibleEditTextField.setVisibility(true);
        onClickSendMsg();
        onImportFile(view);
    }

    private void onClickSendMsg() {
        super.onClickSendMsg(mActivity, addMessage, this,
                "fragmentBtnTextForTranslateContainer");
    }

    @Override
    public String requestToServer(String translateText) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();

        Future<String> future = es.submit(new GetAnswerTranslateFromServerTask
                (translateText, this.getArguments().getString(Constants.KEY_LANGUAGE)));

        es.shutdown();

        return future.get();
    }

    /**
     * Import file from storage
     */
    private void onImportFile(View view) {
        AppCompatButton importFile = view.findViewById(R.id.importBtn);

        importFile.setOnClickListener(view1 -> {
            Intent getFile = new Intent(Intent.ACTION_GET_CONTENT);
            getFile.addCategory(Intent.CATEGORY_OPENABLE);
            getFile.setType("*/*");

            getFileResultLauncher.launch(getFile);
        });
    }

    /**
     * Get file's content
     */
    private final ActivityResultLauncher<Intent> getFileResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    InputStream inputStream = null;
                    InputStreamReader inputStreamReader = null;
                    BufferedReader bufferedReader = null;

                    try {
                        Uri uri = result.getData().getData();

                        inputStream = getContext().getContentResolver().openInputStream(uri);
                        inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

                        bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder fileText = new StringBuilder();

                        String line;
                        while ((line = bufferedReader.readLine()) != null)
                            fileText.append(line);


                        getAnswerFromServer(fileText);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (inputStream != null) inputStream.close();
                            if (inputStreamReader != null) inputStreamReader.close();
                            if (bufferedReader != null) bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );


    /**
     * Get answer from server and add on UI thread
     * @param fileText - import file text
     */
    private void getAnswerFromServer(StringBuilder fileText) {
        if (super.identifyLanguage(fileText.toString(), this.getArguments().getString(Constants.KEY_LANGUAGE))) {
            controlUiComponents(this.getView());
            addMessage.setMessageToContainer(fileText.toString(), null, "",true);
            addMessage.setMessageToContainer(getString(R.string.request_processing), null, "",false);

            Runnable task = () -> {
                String answer;
                try {
                    answer = clearAnswer(requestToServer(fileText.toString()));
                    super.addAnswerOnUIThread(addMessage, answer, new FragmentBtnDownloadText(mActivity), "fragmentBtnDownloadText");
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        addMessage.setMessageToContainer("Отмена запроса", null, "", false);
                    });
                }
            };

            Thread getAnswer = new Thread(task, "translateTextThread");
            getAnswer.start();
        }
    }


    /**
     * Blocking UI after sending of request
     * @param view - this layout
     */
    public void controlUiComponents(View view) {
        EditText inputField = mActivity.findViewById(R.id.inputField);
        inputField.setEnabled(false);
        inputField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_input_field_blocked,
                mActivity.getTheme()));

        AppCompatImageButton sendMsgBtn = mActivity.findViewById(R.id.sendMessage);
        sendMsgBtn.setEnabled(false);
        sendMsgBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.send_msg_blocked,
                mActivity.getTheme()));

        AppCompatButton importBtn = view.findViewById(R.id.importBtn);
        importBtn.setEnabled(false);
        importBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_import_blocked,
                mActivity.getTheme()));
    }
}
