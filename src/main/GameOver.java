package main;

import java.awt.*;
import javax.swing.*;
import units.Unit;

public class GameOver extends JPanel {

    public GameOver(JFrame frame, Unit.Team winner) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        String text;
        if (winner == Unit.Team.ALLY) {
            text = "PLAYER 1 WINS!";
        } else if (winner == Unit.Team.ENEMY) {
            text = "PLAYER 2 WINS!";
        } else {
            text = "DRAW!";
        }

        JLabel title = new JLabel(text, SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));

        JButton backBtn = new JButton("BACK TO MENU");
        backBtn.setPreferredSize(new Dimension(220, 50));
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));

        backBtn.addActionListener(e -> {
            frame.setContentPane(new Menu(frame));
            frame.pack();
            frame.revalidate();
            frame.repaint();
        });

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 20, 40, 20));
        bottom.add(backBtn);

        add(title, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
}
