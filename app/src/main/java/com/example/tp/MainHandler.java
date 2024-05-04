package com.example.tp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.example.tp.generateText.FragmentBtnKeyWords;
import com.example.tp.generateText.FragmentBtnLengthText;
import com.example.tp.interfaces.AddMessage;
import com.example.tp.translateText.FragmentBtnTextForTranslateContainer;

import java.util.concurrent.ExecutionException;

public class MainHandler extends Fragment {
    /**
     * Handler for clicking the send message button
     * @param mActivity - main activity
     * @param addMessage - interface for adding message
     * @param fragment - current fragment
     */
    protected void onClickSendMsg(Activity mActivity, AddMessage addMessage, Fragment fragment) {
        AppCompatImageButton sendMsg = mActivity.findViewById(R.id.sendMessage);
        EditText inputField = mActivity.findViewById(R.id.inputField);

        sendMsg.setOnClickListener(view -> {
            String tag = fragment.getTag();
            String message = inputField.getText().toString();

            if (message.isEmpty()) Toast.makeText(mActivity, R.string.input_text, Toast.LENGTH_SHORT).show();
            else {
                hideKeyBoard(mActivity);
                addMessage.addMessage(message, null, "", true);

                assert tag != null;
                switch (tag) {
                    case "fragmentBtnKeyWords":
                        handlerOfSendMessageForGenerate
                                (fragment, message, addMessage, mActivity);
                        break;
                    case "fragmentBtnTextForTranslateContainer":
                        handlerOfSendMessageForTranslate(fragment, message, addMessage, mActivity);
                        break;
                    case "fragmentBtnChooseArticle":
                        FragmentBtnLengthText f = new FragmentBtnLengthText(mActivity);
                        Bundle args = new Bundle();

                        args.putString("Article", message);
                        f.setArguments(args);

                        addMessage.addMessage(getString(R.string.choose_length_text_msg), f,
                                "fragmentBtnLengthText", false);
                        break;
                }

                inputField.setText("");
            }
        });
    }

    /**
     * Hide keyboard
     * @param mActivity - main activity
     */
    private void hideKeyBoard(Activity mActivity) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View v = mActivity.getCurrentFocus();

        if (v == null) v = new View(mActivity);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Add a message from a secondary stream and
     * makes the transition to the next fragment
     * @param addMessage - interface
     * @param message - adding message
     * @param fragment - next fragment
     * @throws InterruptedException
     */
    protected void addAnswerOnUIThread(AddMessage addMessage, String message, Fragment fragment) throws InterruptedException {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            addMessage.addMessage(message, null, "", false);
            Bundle bundle = new Bundle();

            bundle.putString(Constants.KEY_TEXT, message);
            fragment.setArguments(bundle);

            addMessage.addMessage(getString(R.string.done), fragment, "fragmentBtnDownloadText", false);
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
     * @param fragment - current fragment
     * @param message - message from input field or text file
     * @param addMessage - interface
     * @param mActivity - main activity
     */
    protected void handlerOfSendMessageForTranslate(Fragment fragment, String message,
                                                  AddMessage addMessage, Activity mActivity) {
        Bundle args = fragment.getArguments();
        String keyLanguage = args.getString(Constants.KEY_LANGUAGE);

        boolean correct = identifyLanguage(message, keyLanguage);
        if (correct) {
            Runnable runnable = () -> {
                Handler handler = new Handler(Looper.getMainLooper());
                String answer;
                try {
                    handler.post(() -> {
                        ((FragmentBtnTextForTranslateContainer) fragment).controlUiComponents(true);
                    });
                    answer = ((FragmentBtnTextForTranslateContainer)fragment).requestToServer(message);
                    answer = ((FragmentBtnTextForTranslateContainer) fragment).clearAnswer(answer);

                    addAnswerOnUIThread(addMessage, answer, new FragmentBtnDownloadText(mActivity));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    handler.post(() -> {
                        addMessage.addMessage(getString(R.string.cancel_request), null, "", false);
                    });
                }
            };

            Thread thread = new Thread(runnable, "translateTextThread");
            thread.start();

            addMessage.addMessage(getResources().getString(R.string.request_processing), null, "", false);
        }
    }


    /**
     * Identify the language and equal translate language with identified language
     * @param message - message from input field or text file
     * @param keyLanguage - argument of fragment received on the page of choose language
     * @return - isCorrect
     */
    private boolean identifyLanguage(String message, String keyLanguage) {
        boolean engLanguage = false;
        boolean rusLanguage = false;

        String rusRegex = "[а-яА-Я]+";
        String engRegex = "[a-zA-Z]+";

        message = message.replaceAll("\\s", "");

        if (message.matches(rusRegex)) rusLanguage = true;
        if (message.matches(engRegex)) engLanguage = true;

        if (!rusLanguage && !engLanguage) {
            Toast.makeText(getContext(), R.string.rus_and_eng_letters, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rusLanguage && keyLanguage.equals("0")) return true;
        else if (engLanguage && keyLanguage.equals("1")) return true;
        else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                Toast.makeText(getContext(), R.string.language_equals_translate, Toast.LENGTH_SHORT).show();
            });
            return false;
        }
    }

    /**
     * Handler of sending message from input fields for generate
     * @param fragment - current fragment
     * @param message - key words for generate
     * @param addMessage - interface
     * @param mActivity - main activity
     */
    private void handlerOfSendMessageForGenerate(Fragment fragment, String message,
                                                 AddMessage addMessage, Activity mActivity) {
        Runnable runnable = () -> {
            String answer;

            try {
                answer = ((FragmentBtnKeyWords)fragment).requestToServer(message);
                answer = ((FragmentBtnKeyWords) fragment).clearAnswer(answer);

                FragmentBtnDownloadText f = new FragmentBtnDownloadText(mActivity);
                addAnswerOnUIThread(addMessage, answer, new FragmentBtnDownloadText(mActivity));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    addMessage.addMessage("Отмена запроса", null, "", false);
                });
            }
        };
        Thread thread = new Thread(runnable, "generateTextThread");
        thread.start();

        addMessage.addMessage(getResources().getString(R.string.request_processing), null, "", false);
    }
}
