package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("TEST");
        JFrame frame = new JFrame("Starlight Voyagers");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        Menu menu = new Menu(frame);
        frame.setContentPane(menu);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
    }
}