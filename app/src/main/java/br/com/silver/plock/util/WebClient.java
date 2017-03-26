package br.com.silver.plock.util;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by silver on 25/03/17.
 */

public class WebClient {

    private String mUrl;
    private String mParam;

    public WebClient(String url, String param) {
        this.mUrl = url;
        this.mParam = param;
    }

    public String get(String code) throws IOException {

        String source = String.format("%s?f=%s&pin=%s", this.mUrl, this.mParam, code);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(source)
                .build();

        Response response = client.newCall(request).execute();

        String json = response.body().string();

        return json;
    }
}
