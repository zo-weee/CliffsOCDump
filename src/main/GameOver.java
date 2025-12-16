package main;

import java.awt.*;
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

        /* ===== TITLE ===== */
        String text;
        if (winner == Unit.Team.ALLY) {
            text = "PLAYER 1 WINS!";
        } else if (winner == Unit.Team.ENEMY) {
            text = "PLAYER 2 WINS!";
        } else {
            text = "DRAW!";
        }

        JLabel title = new JLabel(text, SwingConstants.CENTER);
        title.setFont(pixelFontBig);

        if (winner == Unit.Team.ALLY) {
            title.setForeground(new Color(140, 205, 75));
        } else if (winner == Unit.Team.ENEMY) {
            title.setForeground(new Color(205, 75, 75));
        } else {
            title.setForeground(Color.WHITE);
        }

        title.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));
        add(title, BorderLayout.NORTH);

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
                u.isWalking = true;     // 🔥 CRITICAL FIX
                u.updateAnimation();
            }
            animPanel.repaint();
        });
        animTimer.start();

        /* ===== BACK BUTTON ===== */
        JButton backBtn = new JButton("BACK TO MENU");
        backBtn.setPreferredSize(new Dimension(220, 50));
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));

        backBtn.addActionListener(e -> {
            animTimer.stop();
            frame.setContentPane(new Menu(frame));
            frame.pack();
            frame.revalidate();
            frame.repaint();
        });

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 20, 40, 20));
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);
    }
}
