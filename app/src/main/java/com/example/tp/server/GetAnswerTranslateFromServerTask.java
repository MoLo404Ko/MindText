package com.example.tp.server;

import com.example.tp.Constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class GetAnswerTranslateFromServerTask implements Callable<String> {
    private final String translateText;
    private final String language;

    public GetAnswerTranslateFromServerTask(String translateText, String language) {
        this.translateText = translateText;
        this.language = language;
    }

    /**
     * Request to the server for translate text
     * @return - text from server
     */
    @Override
    public String call() throws Exception {
        URL url = new URL(Constants.TRANSLATE_SCRIPT_PATH);

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(8096);
        connection.setRequestProperty("Accept", "text/html");

        String postText = "data=" + translateText + "&language=" + language;

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

        return response.toString();
    }
}
