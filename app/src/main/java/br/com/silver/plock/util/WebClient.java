package br.com.silver.plock.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import br.com.silver.plock.R;

/**
 * Created by silver on 25/03/17.
 */

public class WebClient {

    Activity act;

    public WebClient(Activity act) {
        this.act = act;
    }

    public String get() throws IOException {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        String url = prefs.getString(act.getString(R.string.pref_url), "");
        String param = prefs.getString(act.getString(R.string.pref_url_param), "");

        String source = url+"?f="+param;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(source)
                .build();

        Response response = client.newCall(request).execute();

        String json = response.body().string();

        return json;
    }
}
