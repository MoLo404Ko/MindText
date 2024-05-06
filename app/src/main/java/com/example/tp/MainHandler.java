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
import com.example.tp.generateText.FragmentBtnLengthText;
import com.example.tp.interfaces.AddMessage;
import com.example.tp.tonText.FragmentBtnDoneText;
import com.example.tp.tonText.FragmentBtnTonText;
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
                        if (checkSymbols(message)) {
                            FragmentBtnLengthText f = new FragmentBtnLengthText(mActivity);
                            Bundle args = new Bundle();

                            args.putString("Article", message);
                            f.setArguments(args);

                            addMessage.addMessage(getString(R.string.choose_length_text_msg), f,
                                    "fragmentBtnLengthText", false);
                        }
                        break;
                    case "fragmentBtnTonText":
                        handlerOfSendMessageForTon(fragment, message, addMessage, mActivity);
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
        assert args != null;
        String keyLanguage = args.getString(Constants.KEY_LANGUAGE);

        boolean correct = identifyLanguageForTranslate(message, keyLanguage);
        if (correct) {
            Runnable runnable = () -> {
                Handler handler = new Handler(Looper.getMainLooper());
                String answer;
                try {
                    handler.post(() -> {
                        ((FragmentBtnTextForTranslateContainer) fragment).controlUiComponents(false);
                    });

                    answer = ((FragmentBtnTextForTranslateContainer)fragment).requestToServer(message);
                    answer = ((FragmentBtnTextForTranslateContainer) fragment).clearAnswer(answer);

                    handler.post(() -> {
                        ((FragmentBtnTextForTranslateContainer) fragment).controlUiComponents(true);
                    });

                    addAnswerOnUIThread(addMessage, answer, new FragmentBtnDownloadText(mActivity,
                            fragment.getTag()));

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
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
    private boolean identifyLanguageForTranslate(String message, String keyLanguage) {
        boolean engLanguage = false;
        boolean rusLanguage = false;
        boolean notLetter = false;

        String notLetterRegex = "[\\d.!?-]+";
        String rusRegex = "[а-яА-Я[.!?-]]+";
        String engRegex = "[a-zA-Z[.!?-]]+";

        message = message.replaceAll("\\s", "");

        if (message.matches(rusRegex)) rusLanguage = true;
        if (message.matches(engRegex)) engLanguage = true;
        if (message.matches(notLetterRegex)) notLetter = true;

        if (!rusLanguage && !engLanguage) {
            Toast.makeText(getContext(), R.string.rus_and_eng_letters, Toast.LENGTH_LONG).show();
            return false;
        }
        if (notLetter) {
            Toast.makeText(getContext(), R.string.only_punctuation, Toast.LENGTH_LONG).show();
            return false;
        }

        if (rusLanguage && keyLanguage.equals("0")) return true;
        else if (engLanguage && keyLanguage.equals("1")) return true;
        else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                Toast.makeText(getContext(), R.string.language_equals_translate, Toast.LENGTH_LONG).show();
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
            Handler handler = new Handler(Looper.getMainLooper());

            try {
                handler.post(() -> {
                    ((FragmentBtnKeyWords)fragment).controlUiComponents(false);
                });

                answer = ((FragmentBtnKeyWords)fragment).requestToServer(message);
                answer = ((FragmentBtnKeyWords) fragment).clearAnswer(answer);

                handler.post(() -> {
                    ((FragmentBtnKeyWords)fragment).controlUiComponents(false);
                });

                addAnswerOnUIThread(addMessage, answer, new FragmentBtnDownloadText(mActivity,
                        fragment.getTag()));

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable, "generateTextThread");

        if (checkSymbols(message)) {
            thread.start();
            addMessage.addMessage(getResources().getString(R.string.request_processing), null, "", false);
        }
    }

    protected void handlerOfSendMessageForTon(Fragment fragment, String message,
                                              AddMessage addMessage, Activity mActivity) {
        Runnable runnable = () -> {
            try {
                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(() -> {
                    ((FragmentBtnTonText) fragment).controlUiComponents(false);
                });

                String answer;
                answer = ((FragmentBtnTonText)fragment).requestToServer(message);
                answer = ((FragmentBtnTonText)fragment).clearAnswer(answer);

                String finalAnswer = answer;

                handler.post(() -> {
                    addMessage.addMessage(finalAnswer, null, "", false);
                    addMessage.addMessage(getString(R.string.done), new FragmentBtnDoneText(),
                            "fragmentBtnDoneText", false);
                    ((FragmentBtnTonText) fragment).controlUiComponents(true);
                });

                Log.d("MyLog", answer);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable, "tonThread");
        if (checkSymbols(message)) {
            thread.start();
            addMessage.addMessage(getResources().getString(R.string.request_processing), null, "", false);
        }
    }

    /**
     * Check symbols on rus or eng
     * @param message - message from input field or text file
     * @return - is correct of language
     */
    private boolean checkSymbols(String message) {
        boolean engLanguage = false;
        boolean rusLanguage = false;
        boolean notLetter = false;

        String notLetterRegex = "[\\d.!?-]+";
        String rusRegex = "[а-яА-Я\\d[.!?-]]+";
        String engRegex = "[a-zA-Z\\d[.!?-]]+";

        message = message.replaceAll("\\s", "");

        if (message.matches(rusRegex)) rusLanguage = true;
        if (message.matches(engRegex)) engLanguage = true;
        if (message.matches(notLetterRegex)) notLetter = true;

        if (!rusLanguage && !engLanguage) {
            Toast.makeText(getContext(), R.string.rus_and_eng_letters, Toast.LENGTH_LONG).show();
            return false;
        }
        if (notLetter) {
            Toast.makeText(getContext(), getString(R.string.only_punctuation), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
