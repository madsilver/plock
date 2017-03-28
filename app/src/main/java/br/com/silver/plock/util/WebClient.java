package br.com.silver.plock.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

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

        String source = String.format("%s?f=%s", this.mUrl, this.mParam);

        if(code != null) {
            source += "&code="+code;
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(source)
                .build();

        Response response = client.newCall(request).execute();

        String json = response.body().string();

        return json;
    }

    public static String getSSID(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        String ssid  = info.getSSID();
        return ssid;
    }
}
