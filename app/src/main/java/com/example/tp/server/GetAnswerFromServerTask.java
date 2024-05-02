package com.example.tp.server;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class GetAnswerFromServerTask implements Callable<String> {
    private String scriptPath;
    private String data;

    public GetAnswerFromServerTask(String scriptPath, String data) {
        this.scriptPath = scriptPath;
        this.data = data;
    }

    @Override
    public String call() throws Exception {
        URL url = new URL("https://practicenn.ru/script.php");

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(8096);
//        connection.setRequestProperty("Content-Type", "text/html");
        connection.setRequestProperty("Accept", "text/html");

        Log.d("MyLog", data + " ");
        String postText = "data=" + data;
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
