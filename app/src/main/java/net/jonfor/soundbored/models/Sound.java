package net.jonfor.soundbored.models;

import android.net.Uri;

import net.jonfor.soundbored.BuildConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by jonfor on 12/23/16.
 */

public class Sound {
    private static final String UTF_8 = "UTF-8";
    private Uri soundPath;
    private String soundName;

    public Sound(String soundName) {
        String parsedName = "";
        try {
            parsedName = URLDecoder.decode(soundName, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.soundName = parsedName.substring(0, parsedName.lastIndexOf('.'));

        this.soundPath = Uri.parse(BuildConfig.SITE_URL + "/public/sounds/" + soundName);
    }

    public Uri getSoundPath() {
        return soundPath;
    }

    public String getSoundName() {
        return soundName;
    }
}
