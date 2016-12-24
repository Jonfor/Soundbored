package net.jonfor.soundbored;

import android.content.Context;

import java.io.File;

import okhttp3.Cache;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Jonfor on 12/22/2016.
 */

public class RequestUtil {

    private static OkHttpClient client;

    private static OkHttpClient getOkHttpClient(Context context) {
        if (client == null) {

            File cacheDirectory = createCacheDirectory(context);

            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(cacheDirectory, cacheSize);

            client = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();
        }

        return client;
    }

    private static File createCacheDirectory(Context context) {
        File baseCacheDir = context.getCacheDir();
        // https://groups.google.com/d/msg/android-developers/-694j87eXVU/YYs4b6kextwJ
        if (baseCacheDir != null) {
            return new File(context.getCacheDir(), "HttpResponseCache");
        }

        return null;
    }

    public static void getAllSounds(Context context, Callback callback) {
        String url = BuildConfig.SITE_URL + "/api/sounds";
        Request request = new Request.Builder()
                .url(url)
                .build();

        getOkHttpClient(context).newCall(request).enqueue(callback);
    }

//    public static void postSound(Context context, Callback callback) {
//        String url = webHost + "/api/sounds";
//        Request request = new okhttp3.Request.Builder()
//                .url(url)
//                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
//                .build();
//
//        getOkHttpClient(context).newCall(request).enqueue(callback);
//    }
}
