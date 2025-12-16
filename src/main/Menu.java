package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.*;

public class Menu extends JPanel {

    private Image background;
    private Image titleImage;

    private Font buttonFont;

    // ===== CONSTANTS =====
    private static final int BOX_WIDTH = 360;
    private static final int BOX_HEIGHT = 70;
    private static final int BOX_SPACING = 20;

    private static final int TITLE_Y = 0;
    private static final int TITLE_TARGET_WIDTH = 700;
    private static final int TITLE_SPACING = 40;

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

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setLayout(null); // manual placement

        // ===== LOAD IMAGES =====
        background = new ImageIcon(
            getClass().getResource("/assets/tempbg.png")
        ).getImage();

        titleImage = new ImageIcon(
            getClass().getResource("/assets/ui/title.png")
        ).getImage();

        // ===== LOAD FONT =====
        try {
            InputStream is = getClass().getResourceAsStream(
                "/assets/PixelifySans-VariableFont_wght.ttf"
            );
            buttonFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(28f);
        } catch (FontFormatException | IOException e) {
            buttonFont = new Font("Serif", Font.BOLD, 24);
        }

        // ===== BUTTONS =====
        JButton startButton = new JButton("Start Game");
        JButton exitButton = new JButton("Exit");

        startButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);

        getDamnBorderOutBruh(startButton);
        getDamnBorderOutBruh(exitButton);

        // ===== POSITIONING =====
        int boxX = (width - BOX_WIDTH) / 2;
        int startBoxY = TITLE_Y + getScaledTitleHeight() + TITLE_SPACING;
        int exitBoxY = startBoxY + BOX_HEIGHT + BOX_SPACING;

        startButton.setBounds(boxX, startBoxY, BOX_WIDTH, BOX_HEIGHT);
        exitButton.setBounds(boxX, exitBoxY, BOX_WIDTH, BOX_HEIGHT);

        add(startButton);
        add(exitButton);

        // ===== BUTTON ACTIONS =====
        startButton.addActionListener(e -> {
            frame.setContentPane(
                new CharacterSelect(frame, 1, new ArrayList<>())
            );
            frame.pack();
            frame.revalidate();
            frame.repaint();
        });

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setText("> Start Game <");
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
                exitButton.setText("> Exit <");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exitButton.setText("Exit");
            }
        });
    }

    // ===== SCALE HELPER =====
    private int getScaledTitleHeight() {
        if (titleImage == null) return 0;

        int originalWidth = titleImage.getWidth(this);
        int originalHeight = titleImage.getHeight(this);

        double scale = (double) TITLE_TARGET_WIDTH / originalWidth;
        return (int) (originalHeight * scale);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        // ===== BACKGROUND =====
        if (background != null) {
            g2.drawImage(background, 0, 0, this);
        }

        // ===== TITLE (SCALED) =====
        if (titleImage != null) {

            int scaledHeight = getScaledTitleHeight();
            int x = (getWidth() - TITLE_TARGET_WIDTH) / 2;

            g2.drawImage(
                titleImage,
                x, TITLE_Y,
                TITLE_TARGET_WIDTH, scaledHeight,
                this
            );
        }

        // ===== DRAW "WOODEN" BOXES =====
        int boxX = (getWidth() - BOX_WIDTH) / 2;
        int startBoxY = TITLE_Y + getScaledTitleHeight() + TITLE_SPACING;
        int exitBoxY = startBoxY + BOX_HEIGHT + BOX_SPACING;

        drawWoodBox(g2, boxX, startBoxY);
        drawWoodBox(g2, boxX, exitBoxY);
    }

    private void drawWoodBox(Graphics2D g2, int x, int y) {

        g2.setColor(new Color(90, 60, 30));
        g2.fillRoundRect(x, y, BOX_WIDTH, BOX_HEIGHT, 18, 18);

        g2.setColor(new Color(170, 120, 70));
        g2.fillRoundRect(
            x + 4,
            y + 4,
            BOX_WIDTH - 8,
            BOX_HEIGHT - 8,
            14,
            14
        );

        g2.setColor(new Color(200, 160, 110, 120));
        g2.drawLine(x + 10, y + 12, x + BOX_WIDTH - 10, y + 12);
    }
}
