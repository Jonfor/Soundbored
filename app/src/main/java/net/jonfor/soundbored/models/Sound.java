package net.jonfor.soundbored.models;

import net.jonfor.soundbored.BuildConfig;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by jonfor on 12/23/16.
 */

public class Sound {
    private static final String UTF_8 = "UTF-8";
    private String soundPath;
    private String soundName;
    private String soundExtension;
    private File soundFile;

    public Sound(String soundName) {
        String parsedName = "";
        try {
            parsedName = URLDecoder.decode(soundName, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // The name is everything without the extension
        this.soundName = parsedName.substring(0, parsedName.lastIndexOf('.'));
        this.soundExtension = parsedName.substring(parsedName.lastIndexOf('.'));

        this.soundPath = BuildConfig.SITE_URL + "/public/sounds/" + soundName;
    }

    public String getSoundPath() {
        return soundPath;
    }

    public String getSoundName() {
        return soundName;
    }

    public File getSoundFile() {
        return soundFile;
    }

    public void setSoundFile(File sound) {
        this.soundFile = sound;
    }

    public String getSoundExtension() {
        return soundExtension;
    }
}
