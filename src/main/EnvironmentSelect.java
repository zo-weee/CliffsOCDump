package main;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import units.Unit;

public class EnvironmentSelect extends JPanel {

    private Image background;

    public EnvironmentSelect(JFrame frame,
                             ArrayList<Unit> teamP1,
                             ArrayList<Unit> teamP2) {

        int tileSize = 70;
        int cols = 10;
        int rows = 12;
        int offsetX = 40;
        int offsetY = 40;

        int width = cols * tileSize + offsetX * 2;
        int height = rows * tileSize + offsetY * 2;

        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Select Environment", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.setOpaque(false);

        JButton grass = new JButton("Grass");
        JButton castle = new JButton("Castle");
        JButton ice = new JButton("Ice");

        buttons.add(grass);
        buttons.add(castle);
        buttons.add(ice);

        add(buttons, BorderLayout.CENTER);

        grass.addActionListener(e ->
            startGame(frame, teamP1, teamP2, EnvironmentType.GRASS)
        );

        castle.addActionListener(e ->
            startGame(frame, teamP1, teamP2, EnvironmentType.CASTLE)
        );

        ice.addActionListener(e ->
            startGame(frame, teamP1, teamP2, EnvironmentType.ICE)
        );
    }

    private void startGame(JFrame frame,
                           ArrayList<Unit> teamP1,
                           ArrayList<Unit> teamP2,
                           EnvironmentType env) {

        GameScreen game = new GameScreen(frame, teamP1, teamP2, env);
        frame.setContentPane(game);
        frame.pack();
        frame.revalidate();
        frame.repaint();
    }
}