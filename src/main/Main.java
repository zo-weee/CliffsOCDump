package main;

import audio.MusicPlayer;
import javax.swing.*;
import java.awt.*;

public class Main {

    public static MusicPlayer music = new MusicPlayer();

    public static void main(String[] args) {

        JFrame frame = new JFrame("Starlight Voyagers");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        Image rawIcon = new ImageIcon(
            Main.class.getResource("/assets/ui/icon.png")
        ).getImage();

        // Scale it up cleanly
        Image scaledIcon = rawIcon.getScaledInstance(
            32, 32, Image.SCALE_SMOOTH
        );

frame.setIconImage(scaledIcon);


        Menu menu = new Menu(frame);
        frame.setContentPane(menu);

        music.play(Main.class.getResource("/audio/main.wav"), true);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
