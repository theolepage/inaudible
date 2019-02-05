package com.thdoteo.inaudible;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static AudioResourcesManager audioResourcesManager;
    public static NetworkManager networkManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioResourcesManager = new AudioResourcesManager(this);
        audioResourcesManager.addBackgroundSounds("clic", 14);
        audioResourcesManager.addForegroundSounds("note", 12);

        networkManager = new NetworkManager(this);
        networkManager.askPermissions();

        startAppWhenReady();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        networkManager.onPermissionResult(requestCode, grantResults);
    }

    private void startAppWhenReady()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (networkManager.isReady() && audioResourcesManager.isReady())
                {
                    Intent intent = new Intent(MainActivity.this, AppActivity.class);
                    startActivity(intent);
                } else {
                    startAppWhenReady();
                }
            }
        }, 500);
    }

}