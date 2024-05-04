package com.example.tp.server;

import android.util.Log;

import com.example.tp.Constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;


public class GetAnswerGenerateFromServerTask implements Callable<String> {
    private String promptText;

    public GetAnswerGenerateFromServerTask(String promptText) {
        this.promptText = promptText;
    }

    @Override
    public String call() throws Exception {
        URL url = new URL(Constants.GENERATE_SCRIPT_PATH);

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(8096);
        connection.setRequestProperty("Accept", "text/html");

        String postText = "data=" + promptText;

        byte[] byteData = postText.getBytes(StandardCharsets.UTF_8);

        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(byteData);


        BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

        StringBuilder response = new StringBuilder();
        String responseLine;

        while ((responseLine = br.readLine()) != null)
            response.append(responseLine.trim());

        outputStream.close();
        br.close();
        connection.disconnect();

        Log.d("MyLog", response.toString());
        return response.toString();
    }
}
