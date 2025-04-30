package game.engine;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.Timer;

/**
 * Manages loading, playing, and transitioning between background music tracks in the game.
 * Supports volume control, looping, and crossfading between tracks.
 */
public class SoundManager {

    // Data Members
    private static Clip currentClip;
    private static final Map<String, Clip> tracks = new HashMap<>();
    private static final Map<String, Clip> soundEffects = new HashMap<>();
    private static final Map<String, String> soundEffectPaths = new HashMap<>();
    private static float musicVolume = 0.65f;
    private static float sfxVolume = 0.85f;


    // Methods
    /**
     * Loads all predefined music tracks into memory.
     * This should be called at game startup to preload sounds.
     */
    public static void loadMusic() {
        loadTrack("preparations", "sounds/music/Preparations.wav");
        loadTrack("battle1", "sounds/music/battle1.wav");
        loadTrack("battle2", "sounds/music/battle2.wav");
        loadTrack("battle3", "sounds/music/battle3.wav");
        loadTrack("dragon1", "sounds/music/dragon1.wav");
    }

    public static void loadSoundEffects() {
        loadEffect("criticalHit", "sounds/effects/criticalHit.wav");
        loadEffect("swordSwing", "sounds/effects/swordSwing.wav");
        loadEffect("bowShot", "sounds/effects/bow.wav");
        loadEffect("magicSpell", "sounds/effects/magic_spell.wav");
    }


    /**
     * Loads a single music track into memory and maps it by name.
     *
     * @param name     the unique name used to refer to the track
     * @param filePath the relative file path to the sound file
     */
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

    public static void loadEffect(String name, String filePath) {
        URL resource = SoundManager.class.getResource("/" + filePath);
        if (resource == null) {
            System.err.println("Couldn't find sound effect: " + filePath);
            return;
        }
        // Store path instead of clip
        soundEffectPaths.put(name, "/" + filePath);
    }

    /**
     * Plays a loaded music track by name.
     *
     * @param name the name of the track to play
     * @param loop true to loop the track continuously, false to play once
     */
    public static void playMusic(String name, boolean loop) {
        stopCurrentMusic();

        Clip clip = tracks.get(name); // Get the requested clip
        if (clip != null) {
            currentClip = clip; // ✅ Assign it first!

            setVolume(currentClip, musicVolume); // ✅ Now it's safe to call this

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

    public static void playEffect(String name) {
        String path = soundEffectPaths.get(name);
        if (path == null) {
            System.err.println("Sound effect not loaded: " + name);
            return;
        }

        try {
            URL resource = SoundManager.class.getResource(path);
            if (resource == null) {
                System.err.println("Resource not found for sound effect: " + path);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(resource);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            setVolume(clip, sfxVolume);
            clip.start();
        } catch (Exception e) {
            System.err.println("Failed to play sound effect: " + name);
            e.printStackTrace();
        }
    }



    /**
     * Stops any currently playing music track.
     * Resets the track to the beginning.
     */
    public static void stopCurrentMusic() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.setFramePosition(0);
        }
    }

    /**
     * Applies volume level to a specific audio clip.
     *
     * @param clip   the clip to modify
     * @param volume a float value between 0.0 and 1.0
     */
    private static void setVolume(Clip clip, float volume) {
        if (clip == null) return;

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();
        gainControl.setValue(gain);
    }

    public static void setMusicVolume(float newVolume) {
        musicVolume = Math.max(0f, Math.min(newVolume, 1f));
        if (currentClip != null) {
            setVolume(currentClip, musicVolume);
        }
    }

    public static void setSFXVolume(float newVolume) {
        sfxVolume = Math.max(0f, Math.min(newVolume, 1f));
    }

    public static float getMusicVolume() {
        return musicVolume;
    }

    public static float getSFXVolume() {
        return sfxVolume;
    }

    /**
     * Plays a randomly selected battle-themed music track.
     * Automatically loops and crossfades to it from the current track.
     *
     * @param loop true to loop the selected track
     */
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

    /**
     * Smoothly fades out the current track and fades in the specified track.
     *
     * @param name the name of the new track to transition to
     * @param loop whether the new track should loop
     */
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

        // Fade duration in milliseconds
        final int fadeDuration = 2000;
        final int steps = 40;
        final int delay = fadeDuration / steps;

        Timer timer = new Timer(delay, null);
        final int[] step = {0};

        timer.addActionListener(e -> {
            float progress = step[0] / (float) steps;
            if (oldClip != null && oldClip.isRunning()) {
                setVolume(oldClip, musicVolume * (1.0f - progress));
            }
            setVolume(newClip, musicVolume * progress);

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
