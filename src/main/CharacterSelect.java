package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.*;
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

    private Image background;
    Font headerfont;
    Font pfont;

    JLabel[] teamSlots = new JLabel[4];
    ArrayList<Unit> chosenUnits = new ArrayList<>();
    Unit selectedUnit = null;

    private static final int BIG_PORTRAIT_SIZE = 256;

    static void deboard(JButton b) {
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
    }

    public CharacterSelect(JFrame frame, int playerNumber, ArrayList<Unit> teamP1) {
        this.frame = frame;
        this.playerNumber = playerNumber;
        this.teamP1 = teamP1;

        setBackground(Color.black);
        setLayout(new BorderLayout());

        int tileSize = 70;
        int cols = 10;
        int rows = 12;
        int offsetX = 40;
        int offsetY = 40;

        int width = cols * tileSize + offsetX * 2;
        int height = rows * tileSize + offsetY * 2;
        setPreferredSize(new Dimension(width, height));

        background = new ImageIcon(getClass().getResource("/assets/notsotempbg.png")).getImage();

        try {
            InputStream is = getClass().getResourceAsStream("/assets/PixelifySans-VariableFont_wght.ttf");

            if (is == null) {
                throw new RuntimeException("Font not found on classpath");
            }

            headerfont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(24f);
            pfont = headerfont.deriveFont(16f);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            headerfont = new Font("Serif", Font.BOLD, 36);
            pfont = headerfont.deriveFont(18f);
        }

        /* ========== LEFT PANEL ========== */
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
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
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(centerPanel, BorderLayout.CENTER);

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
        nameLabel.setFont(headerfont);
        statsLabel.setFont(pfont);

        statsPanel.add(nameLabel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(statsLabel);

        infoTop.add(statsPanel, BorderLayout.CENTER);
        centerPanel.add(infoTop, BorderLayout.NORTH);

        /* ========== SKILLS ========== */
        JPanel infoBottom = new JPanel(new BorderLayout());
        infoBottom.setOpaque(false);
        infoBottom.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 20));

        JPanel skillsBox = new JPanel();
        skillsBox.setOpaque(false);
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

        addToTeamBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                addToTeamBtn.setText("> Add to Team <");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                addToTeamBtn.setText("Add to Team");
            }
            @Override
            public void mousePressed(MouseEvent e) {
                addToTeamBtn.setForeground(new Color(140, 205, 75, 255));
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                addToTeamBtn.setForeground(Color.WHITE);
            }
        });

        skillsBox.add(addToTeamBtn);
        infoBottom.add(skillsBox, BorderLayout.CENTER);
        centerPanel.add(infoBottom, BorderLayout.CENTER);

        /* ========== BOTTOM ========== */
        confirmBtn = new JButton(playerNumber == 1 ? "READY" : "START");
        confirmBtn.setPreferredSize(new Dimension(110, 70));
        confirmBtn.setFont(headerfont);

        confirmBtn.addActionListener(e -> confirmTeam());

        confirmBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                confirmBtn.setText(playerNumber == 1 ? "READY?" : "START!");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                confirmBtn.setText(playerNumber == 1 ? "READY" : "START");
            }
        });

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(BorderFactory.createEmptyBorder(5,20,10,20));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JLabel teamHeader = new JLabel("YOUR TEAM MEMBERS:");
        teamHeader.setForeground(Color.WHITE);
        teamHeader.setFont(headerfont);
        teamHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        JPanel slotsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        slotsRow.setOpaque(false);

        for (int i = 0; i < 4; i++) {
            JLabel slot = new JLabel("", SwingConstants.CENTER);
            slot.setPreferredSize(new Dimension(120, 120));
            slot.setOpaque(false);
            // slot.setForeground(Color.WHITE);
            teamSlots[i] = slot;
            slotsRow.add(slot);
        }

        bottomContainer.add(slotsRow, BorderLayout.CENTER);
        bottomContainer.add(confirmBtn, BorderLayout.EAST);
        add(bottomContainer, BorderLayout.SOUTH);

        deboard(addToTeamBtn);
        deboard(confirmBtn);
    }

    /* ================= LOGIC ================= */

    private void confirmTeam() {
    if (chosenUnits.size() != 4) return;

    if (playerNumber == 1) {
        for (Unit u : chosenUnits) {
            u.team = Unit.Team.ALLY;
            u.loadWalkSprites();   // 👈 ADD THIS LINE
        }
        teamP1.addAll(chosenUnits);

        frame.setContentPane(new CharacterSelect(frame, 2, teamP1));
    } else {
        for (Unit u : chosenUnits) {
            u.team = Unit.Team.ENEMY;
            u.loadWalkSprites();   // 👈 ADD THIS LINE
        }

        GameScreen game = new GameScreen(frame, teamP1, chosenUnits);
        frame.setContentPane(game);
    }

    frame.pack();
    frame.revalidate();
    frame.repaint();
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
                u.picLarge.getScaledInstance(
                        BIG_PORTRAIT_SIZE,
                        BIG_PORTRAIT_SIZE,
                        Image.SCALE_SMOOTH
                )
        ));

        basicLabel.setFont(pfont);
        skillLabel.setFont(pfont);
        ultiLabel.setFont(pfont);

        nameLabel.setText("<html>" + u.name + "</html>");
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