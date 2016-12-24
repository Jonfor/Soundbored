package net.jonfor.soundbored;

import net.jonfor.soundbored.models.Sound;

/**
 * Created by jonfor on 12/24/16.
 */

public class APIParsers {

    public static Sound createSound(String soundName) {
        return new Sound(soundName);
    }
}
