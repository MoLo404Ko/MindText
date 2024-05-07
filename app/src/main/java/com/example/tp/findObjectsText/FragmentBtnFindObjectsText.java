package com.example.tp.findObjectsText;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.tp.ClassWorkingWithNN;
import com.example.tp.R;
import com.example.tp.interfaces.AddMessage;
import com.example.tp.interfaces.ControlVisibleEditTextField;
import com.example.tp.interfaces.SetActionBar;
import com.example.tp.interfaces.SetHeightMessageContainer;
import com.example.tp.server.GetAnswerFromFindObjectsServerTask;
import com.example.tp.server.GetAnswerGenerateFromServerTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FragmentBtnFindObjectsText extends ClassWorkingWithNN {
    private AddMessage addMessage;
    private SetHeightMessageContainer setHeightMessageContainer;
    private ControlVisibleEditTextField controlVisibleEditTextField;
    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addMessage = (AddMessage) context;
        setHeightMessageContainer = (SetHeightMessageContainer) context;
        controlVisibleEditTextField = (ControlVisibleEditTextField) context;
    }

    public FragmentBtnFindObjectsText(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_btns_find_objects_text, container, false);
        view.post(() -> setHeightMessageContainer.setHeightMessageContainer(view.getHeight()));

        init(view);
        return view;
    }

    private void init(View view) {
        controlVisibleEditTextField.setVisibility(true);
        onImportFile(view);
        onClickSendMsg();
    }

    /**
     * Request to server for find objects in text
     * @param data - input text
     * @return - objects
     */
    @Override
    public String requestToServer(String data) throws ExecutionException, InterruptedException {
        Bundle args = this.getArguments();

        assert args != null;
        String object = args.getString("Object");

        ExecutorService es = Executors.newSingleThreadExecutor();

        Future<String> future = es.submit(new GetAnswerFromFindObjectsServerTask(object, data));
        es.shutdown();

        return future.get();
    }

    /**
     * Control visible of UI after sending of request
     * @param isVisible - visible of UI
     */
    @Override
    public void controlUiComponents(boolean isVisible) {
        AppCompatButton importBtn = this.getView().findViewById(R.id.importBtn);

        if (isVisible) {
            controlVisibleEditTextField.setVisibility(true);
            importBtn.setEnabled(true);
            importBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_import,
                    mActivity.getTheme()));
        }
        else {
            controlVisibleEditTextField.setVisibility(false);
            importBtn.setEnabled(false);
            importBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_import_blocked,
                    mActivity.getTheme()));
        }
    }

    /**
     * Import file from storage
     * View - layout
     */
    private void onImportFile(View view) {
        AppCompatButton importFile = view.findViewById(R.id.importBtn);

        importFile.setOnClickListener(view1 -> {
            Intent getFile = new Intent(Intent.ACTION_GET_CONTENT);
            getFile.addCategory(Intent.CATEGORY_OPENABLE);
            getFile.setType("text/*");

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
                        assert result.getData() != null;
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
        addMessage.addMessage(fileText.toString(), null, "",true);
        super.handlerOfSendMessageForFindObjects(this, fileText.toString(), addMessage, mActivity);
    }

    /**
     * Handler for clicking on the send button
     */
    private void onClickSendMsg() {
        super.onClickSendMsg(mActivity, addMessage, this);
    }
}
