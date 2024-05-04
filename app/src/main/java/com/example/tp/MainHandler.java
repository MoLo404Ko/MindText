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

import com.example.tp.generateText.FragmentBtnKeyWords;
import com.example.tp.translateText.FragmentBtnTextForTranslateContainer;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainHandler extends Fragment {
    protected void onClickSendMsg(Activity mActivity, AddMessage addMessage, Fragment fragment, String tag) {
        AppCompatImageButton sendMsg = mActivity.findViewById(R.id.sendMessage);
        EditText inputField = mActivity.findViewById(R.id.inputField);

        sendMsg.setOnClickListener(view -> {
            String message = inputField.getText().toString();
            if (message.isEmpty())
                Toast.makeText(mActivity, "Введите текст", Toast.LENGTH_SHORT).show();
            else {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                View v = mActivity.getCurrentFocus();

                if (v == null) v = new View(mActivity);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                addMessage.setMessageToContainer(message, null, "", true);
                addMessage.setMessageToContainer(getResources().getString(R.string.request_processing), null, "", false);

                if (tag.equals("fragmentBtnKeyWords")) {
                    handlerOfSendMessageForGenerate(fragment, message, addMessage, mActivity);
                }
                else if (tag.equals("fragmentBtnTextForTranslateContainer")) {
                    handlerOfSendMessageForTranslate(fragment, message, addMessage, mActivity);
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

//    private void getAnswerFromGigaChat(AddMessage addMessage, Fragment fragment, String prompt) {
//        Runnable task = () -> {
//            String answer;
//            try {
//                answer = GigaChatModelClass.getAnswerFromGigaChat(prompt);
//                Log.d("MyLog", answer);
//                addAnswerOnUIThread(addMessage, answer, fragment, "fragmentBtnDownloadText");
//            } catch (InterruptedException e) {
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(() -> {
//                    addMessage.setMessageToContainer("Отмена запроса", null, "", false);
//                });
//            } catch (JSONException | IOException e) {
//                throw new RuntimeException(e);
//            }
//        };
//
//        Thread getAnswer = new Thread(task, "generateTextThread");
//        getAnswer.start();
//    }

    /**
     * Handler of sending message from input fields for translate
     */
    private void handlerOfSendMessageForTranslate(Fragment fragment, String message,
                                                  AddMessage addMessage, Activity mActivity) {
        Bundle args = fragment.getArguments();
        String keyLanguage = args.getString(Constants.KEY_LANGUAGE);

        boolean correct = identifyLanguage(message, keyLanguage);
        if (correct) {
            Runnable runnable = () -> {
                String answer;
                try {
                    answer = ((FragmentBtnTextForTranslateContainer)fragment).requestToServer(message);
                    answer = ((FragmentBtnTextForTranslateContainer) fragment).clearAnswer(answer);

                    addAnswerOnUIThread(addMessage, answer, new FragmentBtnDownloadText(mActivity), "fragmentBtnDownloadText");
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        addMessage.setMessageToContainer("Отмена запроса", null, "", false);
                    });
                }
            };

            Thread thread = new Thread(runnable, "translateTextThread");
            thread.start();
        }
    }


    /**
     * Identify the language and equal translate language with identified language
     */
    protected boolean identifyLanguage(String message, String keyLanguage) {
        boolean engLanguage = false;
        boolean rusLanguage = false;

        String rusRegex = "[а-яА-Я]+";
        String engRegex = "[a-zA-Z]+";

        message = message.replaceAll("\\s", "");

        if (message.matches(rusRegex)) rusLanguage = true;
        if (message.matches(engRegex)) engLanguage = true;

        if (!rusLanguage && !engLanguage) {
            Toast.makeText(getContext(), "Сообщение содержит английские и русские символовы " +
                    "или специальные символы", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rusLanguage && keyLanguage.equals("0")) return true;
        else if (engLanguage && keyLanguage.equals("1")) return true;
        else {
            Toast.makeText(getContext(), "Введенный язык и язык перевода совпадают", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private void handlerOfSendMessageForGenerate(Fragment fragment, String message,
                                                 AddMessage addMessage, Activity mActivity) {
        Runnable runnable = () -> {
            String answer;

            try {
                answer = ((FragmentBtnKeyWords)fragment).requestToServer(message);
                answer = ((FragmentBtnKeyWords) fragment).clearAnswer(answer);

                addAnswerOnUIThread(addMessage, answer, new FragmentBtnDownloadText(mActivity), "fragmentBtnDownloadText");
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    addMessage.setMessageToContainer("Отмена запроса", null, "", false);
                });
            }
        };
        Thread thread = new Thread(runnable, "generateTextThread");
        thread.start();
    }
}
