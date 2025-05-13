package game.engine;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.Timer;

/**
 * Manages loading, playing, and transitioning between background music tracks in the game.
 * Supports volume control, looping, and crossfading between tracks.
 */
public class SoundManager {

    // Data Members
    private static final ReentrantLock musicLock = new ReentrantLock();
    private static Clip currentClip;
    private static final Map<String, Clip> musicTracks = new HashMap<>();
    private static final Map<String, String> soundEffects = new HashMap<>();
    private static float musicVolume = 0.6f;
    private static float sfxVolume = 0.6f;

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
        loadTrack("lowHP", "sounds/music/lowHP.wav");
        loadTrack("gameOver", "sounds/music/game over.wav");
        loadTrack("winning", "sounds/music/winning.wav");
    }

    /**
     * Loads all predefined sound effects by storing their paths.
     * This should be called at game startup.
     */
    public static void loadSoundEffects() {
        loadEffect("criticalHit", "sounds/effects/criticalHit.wav");
        loadEffect("swordSwing", "sounds/effects/swordSwing.wav");
        loadEffect("bowShot", "sounds/effects/bow.wav");
        loadEffect("magicSpell", "sounds/effects/magic_spell.wav");
        loadEffect("interactTreasure", "sounds/effects/interactTreasure.wav");
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
            musicTracks.put(name, clip);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Failed to load sound: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Loads a sound effect file path by name into memory.
     *
     * @param name  the unique name of the sound effect
     * @param filePath the relative path to the file
     */
    public static void loadEffect(String name, String filePath) {
        URL resource = SoundManager.class.getResource("/" + filePath);
        if (resource == null) {
            System.err.println("Couldn't find sound effect: " + filePath);
            return;
        }
        // Store path instead of clip
        soundEffects.put(name, "/" + filePath);
    }

    /**
     * Plays a loaded music track by name.
     *
     * @param name the name of the track to play
     * @param loop true to loop the track continuously, false to play once
     */
    public static void playMusic(String name, boolean loop) {
        musicLock.lock();
        try {
            stopCurrentMusic();
            Clip clip = musicTracks.get(name);
            if (clip != null) {
                currentClip = clip;
                setVolume(currentClip, musicVolume);
                clip.setFramePosition(0);
                if (loop) {
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
                clip.start();

                // 🚨 Checking if clip is actually running
                forceRestartIfNeeded(name);
            }
            else {
                System.err.println("No music track named: " + name);
            }
        }
        finally {
            musicLock.unlock();
        }
    }

    /**
     * Plays a sound effect by name.
     *
     * @param name the name of the sound effect
     */
    public static void playEffect(String name) {
        String path = soundEffects.get(name);
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
        if (currentClip != null) {
            if (currentClip.isRunning()) {
                currentClip.stop();
            }
            currentClip.setFramePosition(0);  // Reset the clip position
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

    /**
     * Sets the global music volume.
     *
     * @param newVolume float between 0.0 and 1.0
     */
    public static void setMusicVolume(float newVolume) {
        musicVolume = Math.max(0f, Math.min(newVolume, 1f));
        if (currentClip != null) {
            setVolume(currentClip, musicVolume);
        }
    }

    /**
     * Sets the global sound effects volume.
     *
     * @param newVolume float between 0.0 and 1.0
     */
    public static void setSFXVolume(float newVolume) {
        sfxVolume = Math.max(0f, Math.min(newVolume, 1f));
    }

    /**
     * Gets the current music volume.
     *
     * @return music volume (0.0 to 1.0)
     */
    public static float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Gets the current sound effects volume.
     *
     * @return sound effects volume (0.0 to 1.0)
     */
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
        var battleTracks = musicTracks.keySet().stream()
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
        musicLock.lock();
        try {
            System.out.println("Attempting to crossfade to track: " + name);

            Clip newClip = musicTracks.get(name);
            if (newClip == null) {
                System.err.println("Error: Requested track '" + name + "' not found.");
                return;
            }

            if (newClip == currentClip) {
                System.out.println("Track '" + name + "' is already playing. No transition needed.");
                return;
            }

            final Clip oldClip = currentClip;
            currentClip = newClip;

            System.out.println("Transitioning from '" + (oldClip != null ? getTrackName(oldClip) : "None") + "' to '" + name + "'");

            setVolume(newClip, 0f);
            newClip.setFramePosition(0);
            if (loop) {
                newClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            newClip.start();

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
                        System.out.println("Stopping old track: " + getTrackName(oldClip));
                        oldClip.stop();
                        oldClip.setFramePosition(0);
                    }
                    System.out.println("Crossfade completed. '" + name + "' is now active.");
                    System.out.println("Current track playing: " + (currentClip != null && currentClip.isRunning() ? name : "NOT PLAYING"));

                    // 🚨 Checking if clip is actually running
                    forceRestartIfNeeded(name);

                    timer.stop();
                }
            });

            System.out.println("Crossfade initiated for track: " + name);
            timer.start();
        }
        finally {
            musicLock.unlock();
        }
    }

    /**
     * Retrieves the name (key) of a loaded music track based on its {@link Clip} reference.
     *
     * @param clip the clip to identify
     * @return the name of the track, or "Unknown Track" if not found
     */
    private static String getTrackName(Clip clip) {
        return musicTracks.entrySet().stream()
                .filter(entry -> entry.getValue().equals(clip))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Unknown Track");
    }

    /**
     * Ensures that the currently set music track is actually playing.
     * If it is not, attempts to restart it from the beginning.
     *
     * @param name the name of the track expected to be playing
     */
    private static void forceRestartIfNeeded(String name) {
        // 🚨 Force restart if the clip isn't running
        if (!currentClip.isRunning()) {
            System.out.println("WARNING: '" + name + "' was set but isn't playing! Restarting manually...");
            currentClip.setFramePosition(0);
            currentClip.start();
        }
    }
}
