package main;

import audio.MusicPlayer;
import javax.swing.*;

public class Main {

    public static MusicPlayer music = new MusicPlayer();
    public static void main(String[] args) {

        System.out.println("TEST");
        JFrame frame = new JFrame("game test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        Menu menu = new Menu(frame);
        frame.setContentPane(menu);

        music.play(Main.class.getResource("/audio/main.wav"), true);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
    }
}