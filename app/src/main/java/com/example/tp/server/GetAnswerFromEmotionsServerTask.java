package com.example.tp.server;

import com.example.tp.GigaChatModelClass;
import java.util.concurrent.Callable;

public class GetAnswerFromEmotionsServerTask implements Callable<String> {
    private String text;

    public GetAnswerFromEmotionsServerTask(String text) {
        this.text = text;
    }

    @Override
    public String call() throws Exception {
        return GigaChatModelClass.getAnswerFromGigaChat("Какой эмоцинальный окрас у текста. " +
                "Выбери из: радостный, грустный, депрессивный, воодушевленный: " + text);
    }
}
