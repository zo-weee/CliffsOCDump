package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import units.SelectedTeam;

public class Menu extends JPanel {

    private Image background;
    Font pixelfont;
    Font smallpixfont;

    static void getDamnBorderOutBruh(JButton b) {
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
    }

    public Menu(JFrame frame) {

        int tileSize = 70;
        int cols = 10;
        int rows = 12;
        int offsetX = 40;
        int offsetY = 40;

        int width = cols * tileSize + offsetX * 2;
        int height = rows * tileSize + offsetY * 2;

        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);

        background = new ImageIcon("src/assets/tempbg.png").getImage();

        this.setLayout(new GridBagLayout());

        
        try {
            pixelfont = Font.createFont(Font.TRUETYPE_FONT, new File("src/assets/PixelifySans-VariableFont_wght.ttf")).deriveFont(100f);
            smallpixfont = Font.createFont(Font.TRUETYPE_FONT, new File("src/assets/PixelifySans-VariableFont_wght.ttf")).deriveFont(32f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            pixelfont = new Font("Serif", Font.BOLD, 36);
        }

        JLabel title = new JLabel("<html><div style='text-align:right;'>" + "STARLIGHT<br>VOYAGERS" + "</div></html>");
        title.setFont(pixelfont);
        title.setForeground(Color.WHITE);

        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.horizontalAlignment", SwingConstants.RIGHT);
        UIManager.put("Button.font", smallpixfont);

        JButton startButton = new JButton("Start Game");
        JButton exitButton = new JButton("Exit");

        // startButton.setPreferredSize(new Dimension(200, 40));
        // exitButton.setPreferredSize(new Dimension(200, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        this.add(title, gbc);

        gbc.gridy = 1;
        this.add(startButton, gbc);

        gbc.gridy = 2;
        this.add(exitButton, gbc);

        startButton.addActionListener(e -> {
            if (SelectedTeam.team != null && SelectedTeam.team.size() == 4) {
                GameScreen game = new GameScreen(frame, SelectedTeam.team);
                frame.setContentPane(game);
                frame.pack();
                frame.revalidate();
                frame.repaint();

            }
            else {
                CharacterSelect cs = new CharacterSelect(frame);
                frame.setContentPane(cs);
                frame.pack();
                frame.revalidate();
                frame.repaint();
            }
        });

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setText("> Start Game");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setText("Start Game");
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exitButton.setText("> Exit");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exitButton.setText("Exit");
            }
        });


        getDamnBorderOutBruh(startButton);
        getDamnBorderOutBruh(exitButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null) {
            g.drawImage(background, 0, 0, this);
        }
    }

}