package com.example.tp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GigaChatModelClass {

    /**
     * Getting answer from GigaChat
     * @return generated text by prompt text
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    public static String getAnswerFromGigaChat(String prompt) throws JSONException, IOException, InterruptedException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create("scope=" + Constants.SCOPE, mediaType);
        Request request = new Request.Builder()
                .url(Constants.GIGA_CHAT_GET_TOKEN_URL)
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .addHeader("RqUID", Constants.GENERATED_UUID)
                .addHeader("Authorization", "Basic " + Constants.AUTH_DATA)
                .build();
        Response response = client.newCall(request).execute();

        JSONObject object = new JSONObject(response.body().string());
        String token = object.get("access_token").toString();

        return getGeneratedText(token, prompt);
    }

    private static String getGeneratedText(String token, String prompt)
            throws JSONException, IOException {
        JSONObject requestJson = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject item = new JSONObject();

        requestJson.put("model", "GigaChat");

        item.put("role", "user");
        item.put("content", prompt);
        array.put(item);
        requestJson.put("messages", array);

        requestJson.put("temperature", 0.5);
        requestJson.put("max_tokens", 128);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(requestJson.toString(), mediaType);

        Request request = new Request.Builder()
                .url(Constants.GIGA_CHAT_GET_GENERATED_TEXT_URL)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Response response = client.newCall(request).execute();
        JSONObject answerJson = new JSONObject(response.body().string());
        JSONArray answerArray = answerJson.getJSONArray("choices");

        return answerArray.getJSONObject(0).getJSONObject("message").getString("content");
    }
}
