package com.thdoteo.inaudible;

import android.arch.core.util.Function;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class AudioResourcesManager {

    public SoundPool backgroundSoundManager;
    public SoundPool foregroundSoundManager;

    public List<Integer> backgroundSounds = new ArrayList<>();
    public List<Integer> foregroundSounds = new ArrayList<>();

    private int soundsToLoad;
    private int soundsLoaded;

    private Context context;

    private boolean ready;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AudioResourcesManager(Context context)
    {
        this.context = context;

        createSoundManagers();
    }

    public boolean isReady()
    {
        return ready;
    }

    public void addForegroundSounds(String name, int number)
    {
        for (int i = 0; i < number; i++) {
            int resourceId = context.getResources().getIdentifier(name + i, "raw", context.getPackageName());
            foregroundSounds.add(foregroundSoundManager.load(context, resourceId, 1));
            soundsToLoad++;
        }
    }

    public void addBackgroundSounds(String name, int number)
    {
        for (int i = 0; i < number; i++) {
            int resourceId = context.getResources().getIdentifier(name + i, "raw", context.getPackageName());
            backgroundSounds.add(backgroundSoundManager.load(context, resourceId, 1));
            soundsToLoad++;
        }
    }

    SoundPool.OnLoadCompleteListener onSoundManagerReady = new SoundPool.OnLoadCompleteListener() {
        public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
            soundsLoaded++;
            if (soundsLoaded == soundsToLoad)
            {
                ready = true;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createSoundManagers()
    {
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        backgroundSoundManager = new SoundPool.Builder().setAudioAttributes(audioAttributes).build();
        backgroundSoundManager.setOnLoadCompleteListener(onSoundManagerReady);
        foregroundSoundManager = new SoundPool.Builder().setAudioAttributes(audioAttributes).build();
        foregroundSoundManager.setOnLoadCompleteListener(onSoundManagerReady);
    }

}
