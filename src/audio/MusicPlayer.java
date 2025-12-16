package audio;

import javax.sound.sampled.*;
import java.io.File;

public class MusicPlayer {

    private Clip clip;

    public void play(String path, boolean loop) {
        stop(); // stop previous music

        try {
            AudioInputStream ais =
                AudioSystem.getAudioInputStream(new File(path));

            clip = AudioSystem.getClip();
            clip.open(ais);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }

            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    public void setVolume(float volume) {
        if (clip == null) return;

        FloatControl gain =
            (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        // volume: 0.0 = mute, 1.0 = full
        float db = (float) (Math.log10(volume) * 20);
        gain.setValue(db);
    }

    public void playLoop(String path) {
        stop();

        try {
            System.out.println("[BGM] Trying to play: " + path);
            System.out.println("[BGM] CWD: " + new java.io.File(".").getAbsolutePath());
            java.io.File f = new java.io.File(path);
            System.out.println("[BGM] Exists? " + f.exists() + " | " + f.getAbsolutePath());

            AudioInputStream audio = AudioSystem.getAudioInputStream(f);
            clip = AudioSystem.getClip();
            clip.open(audio);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            System.out.println("[BGM] Started OK");

        } catch (Exception e) {
            System.out.println("[BGM] ERROR:");
            e.printStackTrace();
        }
    }

}
