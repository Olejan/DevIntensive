package com.softdesign.devintensive.data.network.interceptor;

import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.managers.PreferencesManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor{

    @Override
    public Response intercept(Chain chain) throws IOException {
        PreferencesManager pm = DataManager.getInstance().getPreferencesManager();

        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
                .header("X-Access-Token", pm.getAuthToken())
                .header("Request_User-Id", pm.getUserId())
                .header("User-Agent", "DevIntensiveApp")
                .header("cache-Control", "max-age=" + (60*60*1));
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
