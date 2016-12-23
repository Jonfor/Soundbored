package net.jonfor.soundbored;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonfor on 10/9/2016.
 */
class Util {
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    static List<Integer> getRawResources() {
        final R.raw rawResources = new R.raw();
        final Class<R.raw> c = R.raw.class;
        final Field[] fields = c.getDeclaredFields();
        List<Integer> resourceIDs = new ArrayList<>();

        for (Field resourceField : fields) {
            /*
             * Skip the argument if it isn't an int. This means it isn't one of the sound resources.
             * https://stackoverflow.com/questions/36235608/public-static-runtime-incremental-change-android
             */
            if (!resourceField.getType().isAssignableFrom(Integer.TYPE)) {
                continue;
            }
            try {
                resourceIDs.add(resourceField.getInt(rawResources));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return resourceIDs;
    }
}
