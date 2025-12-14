package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import units.*;

public class CharacterSelect extends JPanel {

    ArrayList<Unit> allUnits;
    ArrayList<Unit> chosenUnits = new ArrayList<>();
    JFrame frame;

    public CharacterSelect(JFrame frame) {
        this.frame = frame;

        int tileSize = 70;
        int cols = 10;
        int rows = 12;
        int offsetX = 40;
        int offsetY = 40;

        int width = cols * tileSize + offsetX * 2;
        int height = rows * tileSize + offsetY * 2;

        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        allUnits = UnitRegistry.getAllUnits();

        JButton confirmButton = new JButton("Confirm Team");
        confirmButton.setEnabled(false);
        confirmButton.setMaximumSize(new Dimension(200, 40));

        for (Unit u : allUnits) {
            JButton btn = new JButton("Select " + u.name);

            btn.setMaximumSize(new Dimension(200, 40));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);

            btn.addActionListener(e -> {
                if (chosenUnits.size() < 4) {
                    chosenUnits.add(u);
                    btn.setEnabled(false);

                    if (chosenUnits.size() == 4) {
                        confirmButton.setEnabled(true);
                    }
                }
            });

            this.add(Box.createVerticalStrut(10));
            this.add(btn);
        }

        this.add(Box.createVerticalStrut(20));
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(confirmButton);

        confirmButton.addActionListener(e -> {
            startGame();
        });
    }

    private void startGame() {

        for (Unit u : chosenUnits) {
            u.team = Unit.Team.ALLY;
        }

        SelectedTeam.team = chosenUnits;

        GameScreen game = new GameScreen(frame, chosenUnits);
        frame.setContentPane(game);
        frame.pack();
        frame.revalidate();
        frame.repaint();
    }
}