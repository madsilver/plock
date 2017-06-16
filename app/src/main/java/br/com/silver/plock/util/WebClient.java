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

    public WebClient(String url) {
        this.mUrl = url;
    }

    public int request() throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(mUrl)
                .build();

        Response response = client.newCall(request).execute();

        return response.code();
    }

    public static String getSSID(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        String ssid  = info.getSSID();
        return ssid;
    }
}
