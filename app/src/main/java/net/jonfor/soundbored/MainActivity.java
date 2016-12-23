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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        List<Integer> soundResourceIDs = Util.getRawResources();
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        final SoundPool soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(6)
                .build();

        ViewGroup contentMain = (ViewGroup) findViewById(R.id.content_main);
        for (final int resourceId : soundResourceIDs) {
            Button button = createButton(resourceId);
            contentMain.addView(button);

            final int soundId = soundPool.load(MainActivity.this, resourceId, 1);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (soundPool.play(soundId, 1, 1, 0, 0, 1) == 0) {
                        Log.e("Err", "ERROR");
                    }
                }
            });
        }
    }

    private Button createButton(int resourceId) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        Button button = (Button) inflater.inflate(R.layout.button, null);
        button.setId(View.generateViewId());
        String resourceName = getResources().getResourceEntryName(resourceId);
        button.setText(resourceName);
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams
                (FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) Util.convertDpToPixel(20.0f, this),
                (int) Util.convertDpToPixel(10.0f, this), 0);
        button.setLayoutParams(params);

        return button;
    }
}
