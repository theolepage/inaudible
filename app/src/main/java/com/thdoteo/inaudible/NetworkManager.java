package com.thdoteo.inaudible;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NetworkManager {

    private MainActivity context;

    private boolean ready;
    private boolean hasResults;

    private WifiManager wifiManager;

    private List<NetworkEntity> networks = new ArrayList<>();
    private Runnable callback;

    public NetworkManager(MainActivity context)
    {
        this.context = context;
    }

    public void StartScanning()
    {
        ready = true;

        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        context.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    public void onResults(Runnable callback)
    {
        this.callback = callback;
    }

    public List<NetworkEntity> getNetworks()
    {
        return networks;
    }

    public boolean isReady()
    {
        return hasResults;
    }

    /**
     * Register every network detected as a NetworkEntity list.
     */
    private BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                if (!hasResults)
                {
                    hasResults = true;
                }

                List<ScanResult> scanResults = wifiManager.getScanResults();
                networks.clear();
                for (int i = 0; i < scanResults.size(); i++) {
                    NetworkEntity network = new NetworkEntity();
                    network.address = scanResults.get(i).BSSID;
                    network.SSID = scanResults.get(i).SSID;
                    network.signalStrength = wifiManager.calculateSignalLevel(scanResults.get(i).level, 100);
                    network.details = scanResults.get(i).capabilities;
                    networks.add(network);
                }
            }
        }
    };

    /**
     * Ask the user to give the app the location permission.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void askPermissions()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(context, "The location permission is required to run Inaudible.", Toast.LENGTH_SHORT).show();
            } else {
                context.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            StartScanning();
        }
    }

    /**
     * Start application when we get the permission to access the device's location.
     */
    public void onPermissionResult(int requestCode, int[] grantResults)
    {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            StartScanning();
        }
    }

}
