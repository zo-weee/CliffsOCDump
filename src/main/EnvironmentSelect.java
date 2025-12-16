package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import units.Unit;

public class EnvironmentSelect extends JPanel {

    private Image[][] tileImages;
    private Image[] passableTiles;
    private Image tileBlocked;

    private int tileSize = 70;
    private int cols = 10;
    private int rows = 12;
    private int offsetX = 40;
    private int offsetY = 40;

    Font pixelfont;

    static void deboard(JButton b) {
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
    }

    public EnvironmentSelect(JFrame frame,
                             ArrayList<Unit> teamP1,
                             ArrayList<Unit> teamP2) {

        int width = cols * tileSize + offsetX * 2;
        int height = rows * tileSize + offsetY * 2;

        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);
        this.setLayout(new GridBagLayout());

        try {
            InputStream is = getClass().getResourceAsStream("/assets/PixelifySans-VariableFont_wght.ttf");

            if (is == null) {
                throw new RuntimeException("Font not found on classpath");
            }

            pixelfont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(36f);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            pixelfont = new Font("Serif", Font.BOLD, 44);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("Select Environment", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(pixelfont.deriveFont(46f));

        this.add(title, gbc);

        JButton grass = new JButton("Grass");
        grass.setForeground(Color.WHITE);
        grass.setFont(pixelfont.deriveFont(24f));
        JButton castle = new JButton("Castle");
        castle.setForeground(Color.WHITE);
        castle.setFont(pixelfont.deriveFont(24f));
        JButton galaxy = new JButton("Galaxy");
        galaxy.setForeground(Color.WHITE);
        galaxy.setFont(pixelfont.deriveFont(24f));

        gbc.gridy = 1;
        this.add(grass, gbc);
        gbc.gridy = 2;
        this.add(castle, gbc);
        gbc.gridy = 3;
        this.add(galaxy, gbc);

        grass.addActionListener(e ->
            startGame(frame, teamP1, teamP2, EnvironmentType.GRASS)
        );

        castle.addActionListener(e ->
            startGame(frame, teamP1, teamP2, EnvironmentType.CASTLE)
        );

        galaxy.addActionListener(e ->
            startGame(frame, teamP1, teamP2, EnvironmentType.GALAXY)
        );

        grass.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                grass.setText("> Grass <");
                previewEnvironment(EnvironmentType.GRASS);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                grass.setText("Grass");
            }
        });

        castle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                castle.setText("> Castle <");
                previewEnvironment(EnvironmentType.CASTLE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                castle.setText("Castle");
            }
        });

        galaxy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                galaxy.setText("> Galaxy < ");
                previewEnvironment(EnvironmentType.GALAXY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                galaxy.setText("Galaxy");
            }
        });

        deboard(grass);
        deboard(castle);
        deboard(galaxy);

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

    private void loadEnvironment(EnvironmentType env) {
        switch (env) {
            case GRASS:
                passableTiles = new Image[]{
                    new ImageIcon(getClass().getResource("/assets/Environment/env1/pass/grass2.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env1/pass/grass3.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env1/pass/grass4.png")).getImage()
                };

                tileBlocked = new ImageIcon(
                    getClass().getResource("/assets/Environment/env1/block/grass_rock2.png")
                ).getImage();
                break;

            case CASTLE:
                passableTiles = new Image[]{
                    new ImageIcon(getClass().getResource("/assets/Environment/env2/pass/castle1.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env2/pass/castle2.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env2/pass/castle3.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env2/pass/castle4.png")).getImage()
                };

                tileBlocked = new ImageIcon(
                    getClass().getResource("/assets/Environment/env2/block/castle_column2.png")
                ).getImage();
                break;

            case GALAXY:
                passableTiles = new Image[]{
                    new ImageIcon(getClass().getResource("/assets/Environment/env3/pass/star1.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env3/pass/star2.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env3/pass/star3.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env3/pass/star4.png")).getImage()
                };

                tileBlocked = new ImageIcon(
                    getClass().getResource("/assets/Environment/env3/block/star_crystal1.png")
                ).getImage();
                break;
        }
    }

    public void previewEnvironment(EnvironmentType env) {
        loadEnvironment(env);

        tileImages = new Image[rows + 2][cols + 2];
        Random rng = new Random();

        for (int r = 0; r < rows + 2; r++) {
            for (int c = 0; c < cols + 2; c++) {
                tileImages[r][c] = rng.nextDouble() < 0.15 ? tileBlocked : passableTiles[rng.nextInt(passableTiles.length)];
            }
        }

        repaint();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (tileImages == null) return;

        for (int r = 0; r < rows + 2; r++) {
            for (int c = 0; c < cols + 2; c++) {
                g.drawImage(tileImages[r][c], c * tileSize - offsetX, r * tileSize - offsetY, tileSize, tileSize, this);
            }
        }
    }
}