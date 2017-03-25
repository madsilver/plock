package br.com.silver.plock.util;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by silver on 25/03/17.
 */

public class WebClient {

    public String get() throws IOException {

        String url = "https://translate.google.com/#en/pt/on";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        String json = response.body().string();

        return json;
    }
}
