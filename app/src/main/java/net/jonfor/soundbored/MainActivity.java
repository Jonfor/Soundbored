package net.jonfor.soundbored;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.jonfor.soundbored.models.Sound;

import org.apmem.tools.layouts.FlowLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Gson gson = new Gson();
    private List<Sound> sounds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        getAllSounds();
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

    private void getAllSounds() {
        RequestUtil.getAllSounds(MainActivity.this, new Callback() {
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
//                    Util.showNetworkErrorMessage(MainActivity.this);
//                    Util.toggleProgress(false, progressBar);
                    return;
                }

                JsonObject responseObj = gson.fromJson(responseStr, JsonObject.class);
                final JsonArray sounds = responseObj.get("data").getAsJsonArray();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        initSoundButtons(sounds);
                    }
                });
            }
        });
    }

    private void initSoundButtons(JsonArray soundsArr) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        ViewGroup contentMain = (ViewGroup) findViewById(R.id.content_main);
        for (JsonElement soundEle : soundsArr) {
            Sound sound = APIParsers.createSound(soundEle.getAsString());
            sounds.add(sound);
            Button button = createButton(sound);
            contentMain.addView(button);
            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(audioAttributes);
            try {
                mediaPlayer.setDataSource(this, sound.getSoundPath());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.start();
                }
            });
        }
    }
}
