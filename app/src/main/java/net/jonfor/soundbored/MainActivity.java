package net.jonfor.soundbored;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.jonfor.soundbored.models.Sound;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

import static net.jonfor.soundbored.Util.saveSoundsLocally;

public class MainActivity extends AppCompatActivity {

    private Gson gson = new Gson();
    private List<Sound> sounds = new ArrayList<>();
    private Context context;
    private SoundPool soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        // Set up Crashlytics, disabled for debug builds
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        // Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(this, crashlyticsKit);

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

        sounds = Util.getSoundsLocally(context);
        if (sounds.size() == 0) {
            Log.d("Debug", "No local sounds. Retrieving from server.");
            getAllSoundsInfo();
        } else {
            Log.d("Debug", "Sounds are stored locally.");
            for (Sound sound : sounds) {
                initSoundButton(sound);
            }
        }
    }

    private Button createButton(Sound sound) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        Button button = (Button) inflater.inflate(R.layout.button, null);
        button.setId(View.generateViewId());
        button.setText(sound.getSoundName());
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams
                (FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) Util.convertDpToPixel(20.0f, this),
                (int) Util.convertDpToPixel(10.0f, this), 0);
        button.setLayoutParams(params);

        return button;
    }

    private void getAllSoundsInfo() {
        RequestUtil.getAllSoundsInfo(MainActivity.this, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseStr = response.body().string();
                Log.d("responseStr", responseStr);
                if (!response.isSuccessful()) {
                    Log.e("responseStr", response.message());
                    return;
                }

                JsonObject responseObj = gson.fromJson(responseStr, JsonObject.class);
                final JsonArray soundsArr = responseObj.get("data").getAsJsonArray();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        createSounds(soundsArr);
                    }
                });
            }
        });
    }

    private void getSound(final Sound sound) {
        RequestUtil.getSound(MainActivity.this, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("responseStr", response.message());
                    return;
                }
                // https://stackoverflow.com/a/29012988
                String filename = sound.getSoundName() + sound.getSoundExtension();
                final File soundFile = new File(getFilesDir(), filename);
                BufferedSink sink = Okio.buffer(Okio.sink(soundFile));
                sink.writeAll(response.body().source());
                sink.close();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        sound.setSoundFile(soundFile);
                        sounds.add(sound);
                        saveSoundsLocally(sounds, context);
                        initSoundButton(sound);
                    }
                });
            }
        }, sound.getSoundPath());
    }

    private void createSounds(JsonArray soundsArr) {
        for (JsonElement soundEle : soundsArr) {
            Sound sound = APIParsers.createSound(soundEle.getAsString());
            getSound(sound);
        }
    }

    private void initSoundButton(Sound sound) {
        ViewGroup contentMain = (ViewGroup) findViewById(R.id.content_main);
        final Button button = createButton(sound);
        contentMain.addView(button);

        final int soundId = soundPool.load(sound.getSoundFile().getPath(), 1);
        button.setOnClickListener(setSoundButtonClickListener(soundId, sound));
    }

    private View.OnClickListener setSoundButtonClickListener(final int soundId, final Sound sound) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("Played " + sound.getSoundName()));
                if (soundPool.play(soundId, 1, 1, 0, 0, 1) == 0) {
                    Log.e("Err", "ERROR");
                }
            }
        };
    }
}
