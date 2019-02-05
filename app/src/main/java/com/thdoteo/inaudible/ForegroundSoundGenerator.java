package com.thdoteo.inaudible;

import android.os.Handler;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ForegroundSoundGenerator {

    private int numberOfNetworksToPlay = 3;
    private int waitForNetworks = 2000;
    private int notesDelay = 120;

    private NetworkManager networkManager;
    private AudioResourcesManager audioResourcesManager;

    private int currentNetworkId;
    private List<NetworkEntity> networksToPlay = new ArrayList<>();
    private List<Integer> queue = new ArrayList<>();

    public ForegroundSoundGenerator(NetworkManager networkManager, AudioResourcesManager audioResourcesManager)
    {
        this.networkManager = networkManager;
        this.audioResourcesManager = audioResourcesManager;

        createQueueOfNotes();
    }

    private void createQueueOfNotes() {
        queue.clear();

        // If we have played every network, go back to the first one
        if(networksToPlay.size() < (currentNetworkId + 1))
        {
            currentNetworkId = 0;
        }

        // Store (numberOfNetworksToPlay) scanned networks in networks
        if(currentNetworkId == 0) {
            int numberOfNetworksScanned = networkManager.getNetworks().size();

            if(numberOfNetworksScanned < numberOfNetworksToPlay) {
                numberOfNetworksToPlay = numberOfNetworksScanned;
            }

            networksToPlay.clear();
            for(int i = 0; i < numberOfNetworksToPlay; i++) {
                networksToPlay.add(networkManager.getNetworks().get(i));
            }
        }

        // If there are no networks restart this function later
        if (networksToPlay.isEmpty())
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    createQueueOfNotes();
                    return;
                }
            }, waitForNetworks);
        }

        // Choose notes to put in queue
        NetworkEntity network = networksToPlay.get(currentNetworkId);
        for(int i = 0; i < network.SSID.length(); i++) {
            char character = network.SSID.toUpperCase().charAt(i);
            queue.add(getNoteOfCharacter(character));
        }

        // Play and update UI
        AppActivity.onPlayNetwork(network);
        float volume = getVolume(network.signalStrength);
        play(volume);
    }

    private void play(final float volume) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Play note in queue
                int note = queue.get(0);
                audioResourcesManager.foregroundSoundManager.play(note, volume, volume, 0, 0, 1);
                queue.remove(0);

                if(queue.size() > 0) {
                    play(volume);
                } else {
                    // Reset UI
                    AppActivity.onStopNetwork();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Prepare notes for next network
                            currentNetworkId++;
                            createQueueOfNotes();
                        }
                    }, getPauseTime());
                }
            }
        }, notesDelay);
    }

    private int getNoteOfCharacter(char character)
    {
        if (character == 'A' || character == 'M' || character == 'Y') {
            return audioResourcesManager.foregroundSounds.get(0);
        } else if (character == 'B' || character == 'N' || character == 'Z') {
            return audioResourcesManager.foregroundSounds.get(1);
        } else if (character == 'C' || character == 'O' || character == '0') {
            return audioResourcesManager.foregroundSounds.get(2);
        } else if (character == 'D' || character == 'P' || character == '1') {
            return audioResourcesManager.foregroundSounds.get(3);
        } else if (character == 'E' || character == 'Q' || character == '2') {
            return audioResourcesManager.foregroundSounds.get(4);
        } else if (character == 'F' || character == 'R' || character == '3') {
            return audioResourcesManager.foregroundSounds.get(5);
        } else if (character == 'G' || character == 'S' || character == '4') {
            return audioResourcesManager.foregroundSounds.get(6);
        } else if (character == 'H' || character == 'T' || character == '5') {
            return audioResourcesManager.foregroundSounds.get(7);
        } else if (character == 'I' || character == 'U' || character == '6') {
            return audioResourcesManager.foregroundSounds.get(8);
        } else if (character == 'J' || character == 'V' || character == '7') {
            return audioResourcesManager.foregroundSounds.get(9);
        } else if (character == 'K' || character == 'W' || character == '8') {
            return audioResourcesManager.foregroundSounds.get(10);
        } else if (character == 'L' || character == 'X' || character == '9') {
            return audioResourcesManager.foregroundSounds.get(11);
        } else {
            return audioResourcesManager.foregroundSounds.get((int)(Math.random() * 11));
        }
    }

    // 6000ms - 12000ms
    private int getPauseTime() {
        return (int)Math.floor((Math.random() * (12 - 6) + 6) * 1000);
    }

    // 0.4 - 1
    private float getVolume(int signalStrength) {
        if(signalStrength == 0) {
            return 0.4f;
        }
        return (float)(((float)signalStrength / 100) * (1 - 0.4) + 0.4);
    }

}
