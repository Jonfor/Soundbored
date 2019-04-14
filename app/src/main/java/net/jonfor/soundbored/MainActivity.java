package net.jonfor.soundbored;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.apmem.tools.layouts.FlowLayout;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private SoundPool soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(10)
                .build();

        Field[] fields = R.raw.class.getFields();
        for (int currField = 0; currField < fields.length; currField++) {
            try {
                int resourceId = fields[currField].getInt(fields[currField]);
                String resourceName = fields[currField].getName();
                initSoundButton(resourceId, resourceName);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Log.e("Err", e.getMessage());
            }
        }
    }

    private Button createButton(String resourceName) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        Button button = (Button) inflater.inflate(R.layout.button, null);
        button.setId(View.generateViewId());
        button.setText(resourceName);
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams
                (FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) Util.convertDpToPixel(20.0f, this),
                (int) Util.convertDpToPixel(10.0f, this), 0);
        button.setLayoutParams(params);

        return button;
    }

    private void initSoundButton(int resourceId, String resourceName) {
        ViewGroup contentMain = findViewById(R.id.content_main);
        final Button button = createButton(resourceName);
        contentMain.addView(button);

        int soundId = soundPool.load(this, resourceId, 1);
        button.setOnClickListener(setSoundButtonClickListener(soundId));
    }

    private View.OnClickListener setSoundButtonClickListener(final int soundId) {
        return v -> {
            if (soundPool.play(soundId, 1, 1, 0, 0, 1) == 0) {
                Log.e("Err", "ERROR");
            }
        };
    }
}
