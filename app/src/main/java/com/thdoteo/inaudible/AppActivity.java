package com.thdoteo.inaudible;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class AppActivity extends AppCompatActivity {

    private NetworkManager networkManager;
    private AudioResourcesManager audioResourcesManager;
    private ForegroundSoundGenerator foregroundSoundGenerator;
    private BackgroundSoundGenerator backgroundSoundGenerator;

    private static WavesView wavesView;
    private static TextView ssidTextView;
    private static TextView addressTextView;
    private static TextView statsTextView;

    private static int numberOfNetworks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        networkManager = MainActivity.networkManager;
        audioResourcesManager = MainActivity.audioResourcesManager;

        wavesView = findViewById(R.id.canvas);
        ssidTextView = findViewById(R.id.ssid);
        addressTextView = findViewById(R.id.address);
        statsTextView = findViewById(R.id.stats);

        backgroundSoundGenerator = new BackgroundSoundGenerator(networkManager, audioResourcesManager);
        foregroundSoundGenerator = new ForegroundSoundGenerator(networkManager, audioResourcesManager);
    }

    public static void onPlayNetworkBackground(NetworkEntity network, int numberOfNetworksScanned)
    {
        wavesView.setAmplitude(40 + network.signalStrength);

        numberOfNetworks++;
        String plural = (numberOfNetworks > 1) ? "s" : "";
        statsTextView.setText("You have listened to " + numberOfNetworks + " network" + plural + ".");
    }

    public static void onPlayNetwork(NetworkEntity network)
    {
        ssidTextView.setText(network.SSID);
        addressTextView.setText(network.address);

    }

    public static void onStopNetwork()
    {
        ssidTextView.setText("");
        addressTextView.setText("");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
