package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import javax.swing.*;
import java.util.List;
import units.Unit;


public class GameOver extends JPanel {

    private Font pixelFontBig;
    private List<Unit> units;
    private Timer animTimer;

    public GameOver(JFrame frame, Unit.Team winner, List<Unit> winningUnits) {
        this.units = winningUnits;

        /* ===== LOAD PIXEL FONT ===== */
        try {
            InputStream is = GameOver.class.getResourceAsStream(
                "/assets/PixelifySans-VariableFont_wght.ttf"
            );

            if (is == null) throw new RuntimeException("Font not found");

            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .registerFont(baseFont);

            pixelFontBig = baseFont.deriveFont(Font.BOLD, 44f);

        } catch (Exception e) {
            e.printStackTrace();
            pixelFontBig = new Font("Arial", Font.BOLD, 40);
        }

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        String titleText;
        String subtitleText;

        if (winner == Unit.Team.ALLY) {
            titleText = "PLAYER 1 WINS!";
            subtitleText = "The stars shine in your favor!";
        } else if (winner == Unit.Team.ENEMY) {
            titleText = "PLAYER 2 WINS!";
            subtitleText = "The fallen continues to rise...";
        } else {
            titleText = "DRAW!";
            subtitleText = "Fate remains undecided.";
        }

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(pixelFontBig);

        JLabel subtitle = new JLabel(subtitleText, SwingConstants.CENTER);
        subtitle.setFont(pixelFontBig.deriveFont(20f));
        subtitle.setForeground(new Color(220, 220, 220));


        if (winner == Unit.Team.ALLY) {
            title.setForeground(new Color(140, 205, 75));
        } else if (winner == Unit.Team.ENEMY) {
            title.setForeground(new Color(205, 75, 75));
        } else {
            title.setForeground(Color.WHITE);
        }

        title.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(8));
        titleBox.add(subtitle);

        add(titleBox, BorderLayout.NORTH);

        /* ===== ANIMATION PANEL ===== */
        JPanel animPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                if (units == null || units.isEmpty()) return;

                int spacing = 20;
                int spriteWidth = units.get(0).walkFrames[0].getWidth(null);

                int totalWidth =
                        units.size() * spriteWidth
                        + (units.size() - 1) * spacing;

                int startX = (getWidth() - totalWidth) / 2;
                int y = getHeight() / 2 - spriteWidth / 2;

                int x = startX;
                for (Unit u : units) {
                    Image frame = u.walkFrames[u.walkFrameIndex];
                    g2.drawImage(frame, x, y, null);
                    x += spriteWidth + spacing;
                }
            }
        };

        animPanel.setOpaque(false);
        animPanel.setPreferredSize(new Dimension(800, 200));
        add(animPanel, BorderLayout.CENTER);

        /* ===== ENSURE SPRITES ===== */
        for (Unit u : units) {
            if (u.walkFrames == null) {
                u.loadWalkSprites();
            }
            u.walkFrameIndex = 0;
        }

        /* ===== ANIMATION TIMER (FORCE WALKING STATE) ===== */
        animTimer = new Timer(1000 / 10, e -> {
            for (Unit u : units) {
                u.isWalking = true;
                u.updateAnimation();
            }
            animPanel.repaint();
        });
        animTimer.start();

        /* ===== BACK TO MENU BUTTON (MENU STYLE) ===== */
        JPanel bottom = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                int boxWidth = 360;
                int boxHeight = 70;

                int x = (getWidth() - boxWidth) / 2;
                int y = 0;

                // Wooden box (same as Menu)
                g2.setColor(new Color(90, 60, 30));
                g2.fillRoundRect(x, y, boxWidth, boxHeight, 18, 18);

                g2.setColor(new Color(170, 120, 70));
                g2.fillRoundRect(
                    x + 4,
                    y + 4,
                    boxWidth - 8,
                    boxHeight - 8,
                    14,
                    14
                );

                g2.setColor(new Color(200, 160, 110, 120));
                g2.drawLine(x + 10, y + 12, x + boxWidth - 10, y + 12);
            }
        };

        bottom.setOpaque(false);
        bottom.setLayout(null);
        bottom.setPreferredSize(new Dimension(800, 140));
        bottom.setBorder(BorderFactory.createEmptyBorder(30, 20, 40, 20));

        JButton backBtn = new JButton("Back to Menu");
        backBtn.setFont(pixelFontBig.deriveFont(28f));
        backBtn.setForeground(Color.WHITE);

        borderControl(backBtn);

        // center button over box
        int boxWidth = 360;
        int boxHeight = 70;
        int x = (800 - boxWidth) / 2;

        backBtn.setBounds(x, 0, boxWidth, boxHeight);

        backBtn.addActionListener(e -> {
            animTimer.stop();

            Main.music.stop();
            Main.music.play(
                getClass().getResource("/audio/main.wav"),
                true
            );

            frame.setContentPane(new Menu(frame));
            frame.pack();
            frame.revalidate();
            frame.repaint();
        });

        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backBtn.setText("> Back to Menu <");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                backBtn.setText("Back to Menu");
            }
        });

        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

    }

    static void borderControl(JButton b) {
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
    }

}
