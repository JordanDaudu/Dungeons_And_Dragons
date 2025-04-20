package game.engine;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;

public class SoundManager {
    private static Clip currentClip;
    private static final Map<String, Clip> tracks = new HashMap<>();
    private static float volume = 0.65f; // Default volume (0.0 to 1.0)

    public static void loadMusic() {
        loadTrack("preparations", "sounds/Preparations.wav");
        loadTrack("battle1", "sounds/battle1.wav");
        loadTrack("battle2", "sounds/battle2.wav");
        loadTrack("battle3", "sounds/battle3.wav");
        loadTrack("dragon1", "sounds/dragon1.wav");
    }

    private static void loadTrack(String name, String filePath) {
        try {
            URL resource = SoundManager.class.getResource("/" + filePath);
            if (resource == null) {
                System.err.println("Couldn't find sound file: " + filePath);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(resource);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            tracks.put(name, clip);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Failed to load sound: " + filePath);
            e.printStackTrace();
        }
    }

    public static void playMusic(String name, boolean loop) {
        stopCurrentMusic();

        Clip clip = tracks.get(name); // Get the requested clip
        if (clip != null) {
            currentClip = clip; // ✅ Assign it first!

            setVolume(currentClip, volume); // ✅ Now it's safe to call this

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.loop(0);
            }

            clip.setFramePosition(0);
            clip.start();
        } else {
            System.err.println("No music track named: " + name);
        }
    }


    public static void stopCurrentMusic() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.setFramePosition(0);
        }
    }

    public static void setVolume(float newVolume) {
        volume = Math.max(0f, Math.min(newVolume, 1f)); // Clamp to [0, 1]
        if (currentClip != null) {
            setVolume(currentClip, volume);
        }
    }

    private static void setVolume(Clip clip, float volume) {
        if (clip == null) return;

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();
        gainControl.setValue(gain);
    }

    public static void playRandomBattleTrack(boolean loop) {
        // Filter for battle tracks
        var battleTracks = tracks.keySet().stream()
                .filter(name -> name.startsWith("battle"))
                .toList();

        if (battleTracks.isEmpty()) {
            System.err.println("No battle tracks loaded!");
            return;
        }

        // Pick a random battle track
        int index = RandomUtil.getRandomInt(battleTracks.size());
        String selectedTrack = battleTracks.get(index);

        // Crossfade to it
        crossfadeTo(selectedTrack, loop);
    }

    public static void crossfadeTo(String name, boolean loop) {
        Clip newClip = tracks.get(name);
        if (newClip == null) {
            System.err.println("No music track named: " + name);
            return; // clip not found
        }
        else if(newClip == currentClip) {
            return; // No need to switch, same track
        }

        final Clip oldClip = currentClip;
        currentClip = newClip;

        try {
            setVolume(newClip, 0f);
            newClip.setFramePosition(0);
            if (loop) {
                newClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                newClip.loop(0);
            }
            newClip.start();
        } catch (Exception e) {
            System.err.println("Error starting new clip: " + e.getMessage());
            return;
        }

        // Fade duration in ms
        final int fadeDuration = 2000;
        final int steps = 40;
        final int delay = fadeDuration / steps;

        Timer timer = new Timer(delay, null);
        final int[] step = {0};

        timer.addActionListener(e -> {
            float progress = step[0] / (float) steps;
            if (oldClip != null && oldClip.isRunning()) {
                setVolume(oldClip, volume * (1.0f - progress));
            }
            setVolume(newClip, volume * progress);

            step[0]++;
            if (step[0] > steps) {
                if (oldClip != null) {
                    oldClip.stop();
                    oldClip.setFramePosition(0);
                }
                timer.stop();
            }
        });

        timer.start();
    }

}
