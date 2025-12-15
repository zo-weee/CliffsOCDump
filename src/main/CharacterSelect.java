package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import units.*;

public class CharacterSelect extends JPanel {

    private JFrame frame;
    private int playerNumber;           // 1 or 2
    private ArrayList<Unit> teamP1;     // passed forward

    JLabel bigPortrait;
    JLabel nameLabel;
    JLabel statsLabel;
    JLabel basicLabel;
    JLabel skillLabel;
    JLabel ultiLabel;
    JButton addToTeamBtn;
    JButton confirmBtn;

    JLabel[] teamSlots = new JLabel[4];
    ArrayList<Unit> chosenUnits = new ArrayList<>();
    Unit selectedUnit = null;

    private static final int BIG_PORTRAIT_SIZE = 256;

    public CharacterSelect(JFrame frame, int playerNumber, ArrayList<Unit> teamP1) {
        this.frame = frame;
        this.playerNumber = playerNumber;
        this.teamP1 = teamP1;

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        int tileSize = 70;
        int cols = 10;
        int rows = 12;
        int offsetX = 40;
        int offsetY = 40;

        int width = cols * tileSize + offsetX * 2;
        int height = rows * tileSize + offsetY * 2;
        setPreferredSize(new Dimension(width, height));

        /* ========== LEFT PANEL ========== */
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        add(leftPanel, BorderLayout.WEST);

        for (Unit u : UnitRegistry.getAllUnits()) {
            JButton portraitBtn = new JButton(new ImageIcon(u.picSmall));
            portraitBtn.setBorderPainted(false);
            portraitBtn.setContentAreaFilled(false);
            portraitBtn.setFocusPainted(false);

            portraitBtn.addActionListener(e -> {
                selectedUnit = u;
                showUnitInfo(u);
            });

            leftPanel.add(portraitBtn);
            leftPanel.add(Box.createVerticalStrut(10));
        }

        /* ========== CENTER PANEL ========== */
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.BLACK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(centerPanel, BorderLayout.CENTER);

        JPanel infoTop = new JPanel(new BorderLayout());
        infoTop.setBackground(Color.BLACK);

        bigPortrait = new JLabel();
        bigPortrait.setPreferredSize(new Dimension(BIG_PORTRAIT_SIZE, BIG_PORTRAIT_SIZE));
        bigPortrait.setBorder(BorderFactory.createEmptyBorder(70, 0, 0, 40));
        infoTop.add(bigPortrait, BorderLayout.WEST);

        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(Color.BLACK);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(70, 40, 0, 0));
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        nameLabel = new JLabel();
        statsLabel = new JLabel();
        nameLabel.setForeground(Color.WHITE);
        statsLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        statsPanel.add(nameLabel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(statsLabel);

        infoTop.add(statsPanel, BorderLayout.CENTER);
        centerPanel.add(infoTop, BorderLayout.NORTH);

        /* ========== SKILLS ========== */
        JPanel infoBottom = new JPanel(new BorderLayout());
        infoBottom.setBackground(Color.BLACK);
        infoBottom.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 20));

        JPanel skillsBox = new JPanel();
        skillsBox.setBackground(Color.BLACK);
        skillsBox.setLayout(new BoxLayout(skillsBox, BoxLayout.Y_AXIS));

        basicLabel = new JLabel();
        skillLabel = new JLabel();
        ultiLabel = new JLabel();
        for (JLabel l : new JLabel[]{basicLabel, skillLabel, ultiLabel}) {
            l.setForeground(Color.WHITE);
        }

        skillsBox.add(basicLabel);
        skillsBox.add(Box.createVerticalStrut(10));
        skillsBox.add(skillLabel);
        skillsBox.add(Box.createVerticalStrut(10));
        skillsBox.add(ultiLabel);
        skillsBox.add(Box.createVerticalStrut(20));

        addToTeamBtn = new JButton("Add to Team");
        addToTeamBtn.setVisible(false);

        addToTeamBtn.addActionListener(e -> {
            if (selectedUnit == null) return;
            if (chosenUnits.size() >= 4) return;

            Unit copy = selectedUnit.createCopy(); // IMPORTANT
            chosenUnits.add(copy);

            int idx = chosenUnits.size() - 1;
            teamSlots[idx].setIcon(new ImageIcon(selectedUnit.picSmall));
            teamSlots[idx].setText("");
        });

        skillsBox.add(addToTeamBtn);
        infoBottom.add(skillsBox, BorderLayout.CENTER);
        centerPanel.add(infoBottom, BorderLayout.CENTER);

        /* ========== BOTTOM ========== */
        confirmBtn = new JButton(playerNumber == 1 ? "READY" : "START");
        confirmBtn.setPreferredSize(new Dimension(110, 70));
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 14));

        confirmBtn.addActionListener(e -> confirmTeam());

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(Color.BLACK);
        bottomContainer.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        JPanel slotsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        slotsRow.setBackground(Color.BLACK);

        for (int i = 0; i < 4; i++) {
            JLabel slot = new JLabel("[Empty]", SwingConstants.CENTER);
            slot.setPreferredSize(new Dimension(120, 120));
            slot.setOpaque(true);
            slot.setBackground(Color.DARK_GRAY);
            slot.setForeground(Color.WHITE);
            teamSlots[i] = slot;
            slotsRow.add(slot);
        }

        bottomContainer.add(slotsRow, BorderLayout.CENTER);
        bottomContainer.add(confirmBtn, BorderLayout.EAST);
        add(bottomContainer, BorderLayout.SOUTH);
    }

    /* ================= LOGIC ================= */

    private void confirmTeam() {
        if (chosenUnits.size() != 4) return;

        if (playerNumber == 1) {
            for (Unit u : chosenUnits) u.team = Unit.Team.ALLY;
            teamP1.addAll(chosenUnits);

            frame.setContentPane(new CharacterSelect(frame, 2, teamP1));
        } else {
            for (Unit u : chosenUnits) u.team = Unit.Team.ENEMY;

            GameScreen game = new GameScreen(frame, teamP1, chosenUnits);
            frame.setContentPane(game);
        }

        frame.pack();
        frame.revalidate();
        frame.repaint();
    }

    private void showUnitInfo(Unit u) {
        bigPortrait.setIcon(new ImageIcon(
                u.picLarge.getScaledInstance(
                        BIG_PORTRAIT_SIZE,
                        BIG_PORTRAIT_SIZE,
                        Image.SCALE_SMOOTH
                )
        ));

        nameLabel.setText("Player " + playerNumber + ": " + u.name);
        statsLabel.setText(
                "<html>Role: " + u.role + "<br><br>"
                        + "HP: " + u.maxHp + "<br>"
                        + "ATK: " + u.atk + "<br>"
                        + "DEF: " + u.def + "<br>"
                        + "Move: " + u.moveRange + "<br>"
                        + "Range: " + u.attackRange + "</html>"
        );

        basicLabel.setText("<html><b>Basic:</b> " + u.basicDesc + "</html>");
        skillLabel.setText("<html><b>Skill:</b> " + u.skillDesc + "</html>");
        ultiLabel.setText("<html><b>Ultimate:</b> " + u.ultimateDesc + "</html>");

        addToTeamBtn.setVisible(true);
    }
}
