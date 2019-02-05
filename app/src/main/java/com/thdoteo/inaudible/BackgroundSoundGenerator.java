package com.thdoteo.inaudible;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class BackgroundSoundGenerator {

    private float clicksVolume = 0.9f;
    private int waitForNetworks = 2000;

    private AudioResourcesManager audioResourcesManager;
    private NetworkManager networkManager;

    private int currentNetworkId;
    private List<NetworkEntity> networks = new ArrayList<>();
    private List<Integer> queue = new ArrayList<>();

    public BackgroundSoundGenerator(
            NetworkManager networkManager,
            AudioResourcesManager audioResourcesManager
    )
    {
        this.networkManager = networkManager;
        this.audioResourcesManager = audioResourcesManager;

        createQueueOfClicks();
    }

    private void createQueueOfClicks() {
        queue.clear();

        // If we have played every network, go back to the first one
        if(networks.size() < (currentNetworkId + 1)) {
            currentNetworkId = 0;
        }

        if(currentNetworkId == 0) {
            networks = networkManager.getNetworks();
        }

        // If there are no networks restart this function later
        if (networks.isEmpty())
        {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    createQueueOfClicks();
                    return;
                }
            }, waitForNetworks);
        }

        // Choose clicks to put in queue
        NetworkEntity network = networks.get(currentNetworkId);
        for(int i = 0; i < network.SSID.length(); i++) {
            char character = network.SSID.toUpperCase().charAt(i);
            queue.add(getClickOfCharacter(character));
        }

        AppActivity.onPlayNetworkBackground(network, networks.size());
        play(network);
    }

    private void play(final NetworkEntity network) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Play click in queue
                int click = queue.get(0);
                audioResourcesManager.backgroundSoundManager.play(click, clicksVolume, clicksVolume, 0, 0, 1);
                queue.remove(0);

                if(queue.size() > 0) {
                    play(network);
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Prepare clicks for next network
                            currentNetworkId++;
                            createQueueOfClicks();
                        }
                    }, getPauseTime());
                }
            }
        }, getDelayTime(network.signalStrength));
    }

    private int getClickOfCharacter(char character)
    {
        if (character == 'A' || character == 'M' || character == 'Y') {
            return audioResourcesManager.backgroundSounds.get(0);
        } else if (character == 'B' || character == 'N' || character == 'Z') {
            return audioResourcesManager.backgroundSounds.get(1);
        } else if (character == 'C' || character == 'O' || character == '0') {
            return audioResourcesManager.backgroundSounds.get(2);
        } else if (character == 'D' || character == 'P' || character == '1') {
            return audioResourcesManager.backgroundSounds.get(3);
        } else if (character == 'E' || character == 'Q' || character == '2') {
            return audioResourcesManager.backgroundSounds.get(4);
        } else if (character == 'F' || character == 'R' || character == '3') {
            return audioResourcesManager.backgroundSounds.get(5);
        } else if (character == 'G' || character == 'S' || character == '4') {
            return audioResourcesManager.backgroundSounds.get(6);
        } else if (character == 'H' || character == 'T' || character == '5') {
            return audioResourcesManager.backgroundSounds.get(7);
        } else if (character == 'I' || character == 'U' || character == '6') {
            return audioResourcesManager.backgroundSounds.get(8);
        } else if (character == 'J' || character == 'V' || character == '7') {
            return audioResourcesManager.backgroundSounds.get(9);
        } else if (character == 'K' || character == 'W' || character == '8') {
            return audioResourcesManager.backgroundSounds.get(10);
        } else if (character == 'L' || character == 'X' || character == '9') {
            return audioResourcesManager.backgroundSounds.get(11);
        } else if (character == ' ' || character == '_' || character == '-') {
            return audioResourcesManager.backgroundSounds.get(12);
        } else {
            return audioResourcesManager.backgroundSounds.get(13);
        }
    }

    // 0ms - 1600ms + RANDOM
    private int getPauseTime() {
        if(networks.size() == 0) {
            return 1600 + (int)Math.floor(Math.random() * 800);
        }
        return (2 / networks.size()) * 800 + (int)Math.floor(Math.random() * 800);
    }

    // 0ms - 1000ms + RANDOM
    private int getDelayTime(int signalStrength) {
        if(signalStrength == 0) {
            return 1000 + (int)Math.floor(Math.random() * 300);
        }
        return (int)Math.floor((1 / signalStrength)) * 1000 + (int)Math.floor(Math.random() * 300);
    }

}
