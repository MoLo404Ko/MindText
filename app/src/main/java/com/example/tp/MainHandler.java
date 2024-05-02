package com.example.tp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import org.json.JSONException;

import java.io.IOException;

public class MainHandler extends Fragment {
    protected void onClickSendMsg(Activity mActivity, AddMessage addMessage, Fragment fragment, String tag) {
        AppCompatImageButton sendMsg = mActivity.findViewById(R.id.sendMessage);
        EditText inputField = mActivity.findViewById(R.id.inputField);

        sendMsg.setOnClickListener(view -> {
            if (inputField.getText().toString().isEmpty())
                Toast.makeText(mActivity, "Введите текст", Toast.LENGTH_SHORT).show();
            else {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                View v = mActivity.getCurrentFocus();

                if (v == null) v = new View(mActivity);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                addMessage.setMessageToContainer(inputField.getText().toString(), null, "", true);
                addMessage.setMessageToContainer(getResources().getString(R.string.request_processing), null, "", false);

                if (tag.equals("fragmentBtnKeyWords")) {
                    Bundle bundle = fragment.getArguments();
                    String prompt = "Напишите текст на тему " + bundle.get("Article") + " длиной до " + bundle.getString("Length") +
                            " предложений с ключевыми словами " + inputField.getText();
                    getAnswerFromGigaChat(addMessage, new FragmentBtnDownloadText(mActivity), prompt);
                }
                else {
                    try {
                        addAnswerOnUIThread(addMessage, inputField.getText().toString(), fragment, tag);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                inputField.setText("");
            }
        });
    }

    protected void addAnswerOnUIThread(AddMessage addMessage, String data, Fragment fragment, String tag) throws InterruptedException {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            addMessage.setMessageToContainer(data, null, "", false);
            Bundle bundle = new Bundle();

            if (tag.equals("fragmentBtnDownloadText")) {
                bundle.putString(Constants.KEY_TEXT, data);
                fragment.setArguments(bundle);
            }

            addMessage.setMessageToContainer("Готово", fragment, "fragmentBtnDownloadText", false);
        });
    }

    private void getAnswerFromGigaChat(AddMessage addMessage, Fragment fragment, String prompt) {
        Runnable task = () -> {
            String answer;
            try {
                answer = GigaChatModelClass.getAnswerFromGigaChat(prompt);
                Log.d("MyLog", answer);
                addAnswerOnUIThread(addMessage, answer, fragment, "fragmentBtnDownloadText");
            } catch (InterruptedException e) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    addMessage.setMessageToContainer("Отмена запроса", null, "", false);
                });
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        };

        Thread getAnswer = new Thread(task, "generateTextThread");
        getAnswer.start();
    }
}
