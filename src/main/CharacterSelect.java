package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import units.*;

public class CharacterSelect extends JPanel {

    JLabel bigPortrait;
    JLabel nameLabel;
    JLabel statsLabel;
    JLabel basicLabel;
    JLabel skillLabel;
    JLabel ultiLabel;
    JButton addToTeamBtn;

    JLabel[] teamSlots = new JLabel[4];
    ArrayList<Unit> chosenUnits = new ArrayList<>();
    Unit selectedUnit = null;

    private static final int BIG_PORTRAIT_SIZE = 256;

    public CharacterSelect(JFrame frame) {
        this.setBackground(Color.BLACK);

        int tileSize = 70;
        int cols = 10;
        int rows = 12;
        int offsetX = 40;
        int offsetY = 40;

        int width = cols * tileSize + offsetX * 2;
        int height = rows * tileSize + offsetY * 2;

        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,10));
        this.add(leftPanel, BorderLayout.WEST);

        ArrayList<Unit> allUnits = UnitRegistry.getAllUnits();

        for(Unit u : allUnits){
            JButton portraitBtn = new JButton(new ImageIcon(u.picSmall));
            portraitBtn.setBorderPainted(false);
            portraitBtn.setContentAreaFilled(false);
            portraitBtn.setFocusPainted(false);
            portraitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            portraitBtn.addActionListener(e -> {
                showUnitInfo(u);
                selectedUnit = u;
            });

            leftPanel.add(portraitBtn);
            leftPanel.add(Box.createVerticalStrut(10));
        }

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.BLACK);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        this.add(centerPanel, BorderLayout.CENTER);

        JPanel infoTop = new JPanel(new BorderLayout());
        infoTop.setBackground(Color.BLACK);

        bigPortrait = new JLabel();
        bigPortrait.setPreferredSize(new Dimension(BIG_PORTRAIT_SIZE, BIG_PORTRAIT_SIZE));
        bigPortrait.setHorizontalAlignment(JLabel.CENTER);
        bigPortrait.setVerticalAlignment(JLabel.CENTER);
        bigPortrait.setBorder(BorderFactory.createEmptyBorder(70, 0, 0, 40));
        infoTop.add(bigPortrait, BorderLayout.WEST);

        JPanel statsPanel = new JPanel();
        statsPanel.setBorder(BorderFactory.createEmptyBorder(70, 0, 0, 0));
        statsPanel.setBackground(Color.BLACK);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        nameLabel = new JLabel("");
        statsLabel = new JLabel("");

        nameLabel.setForeground(Color.WHITE);
        statsLabel.setForeground(Color.WHITE);

        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        statsPanel.add(nameLabel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(statsLabel);

        infoTop.add(statsPanel, BorderLayout.CENTER);
        centerPanel.add(infoTop, BorderLayout.NORTH);

        JPanel infoBottom = new JPanel(new BorderLayout());
        infoBottom.setBackground(Color.BLACK);
        infoBottom.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 20));

        JPanel bottomLeft = new JPanel();
        bottomLeft.setBackground(Color.BLACK);
        bottomLeft.setLayout(new BoxLayout(bottomLeft, BoxLayout.Y_AXIS));

        JPanel skillsBox = new JPanel();
        skillsBox.setBackground(Color.BLACK);
        skillsBox.setLayout(new BoxLayout(skillsBox, BoxLayout.Y_AXIS));
        skillsBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        basicLabel = new JLabel("");
        skillLabel = new JLabel("");
        ultiLabel = new JLabel("");

        basicLabel.setForeground(Color.WHITE);
        skillLabel.setForeground(Color.WHITE);
        ultiLabel.setForeground(Color.WHITE);

        basicLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        skillLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ultiLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        skillsBox.add(basicLabel);
        skillsBox.add(Box.createVerticalStrut(10));
        skillsBox.add(skillLabel);
        skillsBox.add(Box.createVerticalStrut(10));
        skillsBox.add(ultiLabel);

        bottomLeft.add(skillsBox);
        bottomLeft.add(Box.createVerticalStrut(20));

        addToTeamBtn = new JButton("Add to Team");
        addToTeamBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addToTeamBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addToTeamBtn.setVisible(false);

        addToTeamBtn.addActionListener(e -> {
            if (selectedUnit == null) return;
            if (chosenUnits.size() < 4 && !chosenUnits.contains(selectedUnit)) {
                chosenUnits.add(selectedUnit);
                int idx = chosenUnits.size() - 1;
                teamSlots[idx].setIcon(new ImageIcon(selectedUnit.picSmall));
                teamSlots[idx].setText("");
                teamSlots[idx].setBackground(Color.BLACK);
            }
        });

        bottomLeft.add(addToTeamBtn);
        infoBottom.add(bottomLeft, BorderLayout.CENTER);
        centerPanel.add(infoBottom, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5,20,10,20));

        JLabel teamHeader = new JLabel("YOUR TEAM MEMBERS:");
        teamHeader.setForeground(Color.WHITE);
        teamHeader.setFont(new Font("Arial", Font.BOLD, 20));
        teamHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(teamHeader);
        bottomPanel.add(Box.createVerticalStrut(10));

        JPanel slotsRow = new JPanel();
        slotsRow.setBackground(Color.BLACK);
        slotsRow.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        for (int i = 0; i < 4; i++) {
            JLabel slot = new JLabel("[Empty]");
            slot.setPreferredSize(new Dimension(120, 120));
            slot.setOpaque(true);
            slot.setBackground(Color.DARK_GRAY);
            slot.setHorizontalAlignment(SwingConstants.CENTER);
            slot.setForeground(Color.WHITE);

            int index = i;
            slot.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    removeUnitFromTeam(index);
                }
            });

            teamSlots[i] = slot;
            slotsRow.add(slot);
        }

        bottomPanel.add(slotsRow);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

        private void showUnitInfo(Unit u){
        Image scaled = u.picLarge.getScaledInstance(
            BIG_PORTRAIT_SIZE,
            BIG_PORTRAIT_SIZE,
            Image.SCALE_SMOOTH
        );
        bigPortrait.setIcon(new ImageIcon(scaled));

        nameLabel.setText(u.name);
        statsLabel.setText(
            "<html>"
                + "Role: " + u.role + "<br><br>"
                + "HP: " + u.maxHp + "<br>"
                + "ATK: " + u.atk + "<br>"
                + "Magic ATK: " + u.magicAtk + "<br>"
                + "DEF: " + u.def + "<br>"
                + "Energy: " + u.energy + "<br><br>"
                + "Move Range: " + u.moveRange + "<br>"
                + "Attack Range: " + u.attackRange
                + "</html>"
        );

        basicLabel.setText("<html><b>Basic:</b> " + u.basicDesc + "</html>");
        skillLabel.setText("<html><b>Skill:</b> " + u.skillDesc + "</html>");
        ultiLabel.setText("<html><b>Ultimate:</b> " + u.ultimateDesc + "</html>");

        addToTeamBtn.setVisible(true);
        revalidate();
        repaint();
    }

    private void startGame() {
        for (Unit u : chosenUnits) {
            u.team = Unit.Team.ALLY;
        }

        SelectedTeam.team = chosenUnits;
    }

    private void removeUnitFromTeam(int index) {
        if (index >= chosenUnits.size()) return;

        chosenUnits.remove(index);

        for (int i = 0; i < 4; i++) {
            if (i < chosenUnits.size()) {
                Unit updated = chosenUnits.get(i);
                teamSlots[i].setIcon(new ImageIcon(updated.picSmall));
                teamSlots[i].setText("");
                teamSlots[i].setBackground(Color.BLACK);
            } else {
                teamSlots[i].setIcon(null);
                teamSlots[i].setText("[Empty]");
                teamSlots[i].setBackground(Color.DARK_GRAY);
            }
        }
    }
}
