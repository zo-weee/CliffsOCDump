package main;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import units.*;

public class CharacterSelect extends JPanel {

    private JFrame frame;
    JLabel bigPortrait;
    JLabel nameLabel;
    JLabel statsLabel;
    JLabel basicLabel;
    JLabel skillLabel;
    JLabel ultiLabel;
    JButton addToTeamBtn;
    JButton startGameBtn;

    private Image background;
    Font headerfont;
    Font pfont;

    JLabel[] teamSlots = new JLabel[4];
    ArrayList<Unit> chosenUnits = new ArrayList<>();
    Unit selectedUnit = null;

    private static final int BIG_PORTRAIT_SIZE = 256;

    public CharacterSelect(JFrame frame) {
        this.frame = frame;
        this.setBackground(Color.BLACK);
        this.setLayout(new BorderLayout());

        int tileSize = 70;
        int cols = 10;
        int rows = 12;
        int offsetX = 40;
        int offsetY = 40;

        int width = cols * tileSize + offsetX * 2;
        int height = rows * tileSize + offsetY * 2;

        background = new ImageIcon("src/assets/notsotempbg.png").getImage();

        this.setPreferredSize(new Dimension(width, height));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,10));
        this.add(leftPanel, BorderLayout.WEST);

        for (Unit u : UnitRegistry.getAllUnits()) {
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

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        this.add(centerPanel, BorderLayout.CENTER);

        JPanel infoTop = new JPanel(new BorderLayout());
        infoTop.setOpaque(false);

        bigPortrait = new JLabel();
        bigPortrait.setPreferredSize(new Dimension(BIG_PORTRAIT_SIZE, BIG_PORTRAIT_SIZE));
        bigPortrait.setBorder(BorderFactory.createEmptyBorder(70, 0, 0, 40));
        infoTop.add(bigPortrait, BorderLayout.WEST);

        JPanel statsPanel = new JPanel();
        statsPanel.setOpaque(false);
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

        JPanel infoBottom = new JPanel(new BorderLayout());
        infoBottom.setOpaque(false);
        infoBottom.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 20));

        JPanel skillsBox = new JPanel();
        skillsBox.setOpaque(false);
        skillsBox.setLayout(new BoxLayout(skillsBox, BoxLayout.Y_AXIS));

        basicLabel = new JLabel();
        skillLabel = new JLabel();
        ultiLabel = new JLabel();
        basicLabel.setForeground(Color.WHITE);
        skillLabel.setForeground(Color.WHITE);
        ultiLabel.setForeground(Color.WHITE);

        skillsBox.add(basicLabel);
        skillsBox.add(Box.createVerticalStrut(10));
        skillsBox.add(skillLabel);
        skillsBox.add(Box.createVerticalStrut(10));
        skillsBox.add(ultiLabel);
        skillsBox.add(Box.createVerticalStrut(20));

        addToTeamBtn = new JButton("Add to Team");
        addToTeamBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addToTeamBtn.setVisible(false);

        addToTeamBtn.addActionListener(e -> {
            if (selectedUnit == null) return;
            if (chosenUnits.size() < 4 && !chosenUnits.contains(selectedUnit)) {
                chosenUnits.add(selectedUnit);
                int idx = chosenUnits.size() - 1;
                teamSlots[idx].setIcon(new ImageIcon(selectedUnit.picSmall));
                teamSlots[idx].setText("");
            }
        });

        skillsBox.add(addToTeamBtn);
        infoBottom.add(skillsBox, BorderLayout.CENTER);
        centerPanel.add(infoBottom, BorderLayout.CENTER);

        startGameBtn = new JButton("START!");
        startGameBtn.setPreferredSize(new Dimension(110, 70));
        startGameBtn.setFont(new Font("Arial", Font.BOLD, 14));

        startGameBtn.addActionListener(e -> {
            if (!chosenUnits.isEmpty()) startGame();
        });

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(BorderFactory.createEmptyBorder(5,20,10,20));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JLabel teamHeader = new JLabel("YOUR TEAM MEMBERS:");
        teamHeader.setForeground(Color.WHITE);
        teamHeader.setFont(new Font("Arial", Font.BOLD, 20));
        teamHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel slotsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        slotsRow.setBackground(Color.BLACK);

        for (int i = 0; i < 4; i++) {
            JLabel slot = new JLabel("[Empty]", SwingConstants.CENTER);
            slot.setPreferredSize(new Dimension(120, 120));
            slot.setOpaque(true);
            slot.setBackground(Color.DARK_GRAY);
            slot.setForeground(Color.WHITE);

            int idx = i;
            slot.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    removeUnitFromTeam(idx);
                }
            });

            teamSlots[i] = slot;
            slotsRow.add(slot);
        }

        bottomPanel.add(teamHeader);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(slotsRow);

        JPanel startPanel = new JPanel(new GridBagLayout());
        startPanel.setBackground(Color.BLACK);
        startPanel.add(startGameBtn);

        bottomContainer.add(bottomPanel, BorderLayout.CENTER);
        bottomContainer.add(startPanel, BorderLayout.EAST);

        this.add(bottomContainer, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null) {
            g.drawImage(background, 0, 0, this);
        }
    }

    private void showUnitInfo(Unit u) {
        bigPortrait.setIcon(new ImageIcon(
            u.picLarge.getScaledInstance(BIG_PORTRAIT_SIZE, BIG_PORTRAIT_SIZE, Image.SCALE_SMOOTH)
        ));

        nameLabel.setText(u.name);
        statsLabel.setText(
            "<html>Role: " + u.role + "<br><br>"
            + "HP: " + u.maxHp + "<br>"
            + "ATK: " + u.atk + "<br>"
            + "Magic ATK: " + u.magicAtk + "<br>"
            + "DEF: " + u.def + "<br>"
            + "Energy: " + u.energy + "<br><br>"
            + "Move Range: " + u.moveRange + "<br>"
            + "Attack Range: " + u.attackRange + "</html>"
        );

        basicLabel.setText("<html><b>Basic:</b> " + u.basicDesc + "</html>");
        skillLabel.setText("<html><b>Skill:</b> " + u.skillDesc + "</html>");
        ultiLabel.setText("<html><b>Ultimate:</b> " + u.ultimateDesc + "</html>");

        addToTeamBtn.setVisible(true);
    }

    private void startGame() {
        if (chosenUnits.size() != 4) return;

        for (Unit u : chosenUnits) {
            u.team = Unit.Team.ALLY;
        }

        SelectedTeam.team = chosenUnits;

        GameScreen game = new GameScreen(frame, SelectedTeam.team);
        frame.setContentPane(game);
        frame.pack();
        frame.revalidate();
        frame.repaint();
    }

    private void removeUnitFromTeam(int index) {
        if (index >= chosenUnits.size()) return;
        chosenUnits.remove(index);

        for (int i = 0; i < 4; i++) {
            if (i < chosenUnits.size()) {
                teamSlots[i].setIcon(new ImageIcon(chosenUnits.get(i).picSmall));
                teamSlots[i].setText("");
            } else {
                teamSlots[i].setIcon(null);
                teamSlots[i].setText("[Empty]");
                teamSlots[i].setBackground(Color.DARK_GRAY);
            }
        }
    }
}
