package main;

import actions.Action;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;
import units.Unit;

public class ActionPanel extends JPanel {

    private JLabel turnLabel;
    private JLabel nameLabel;
    private JLabel hpLabel;
    private JLabel atkLabel;
    private JLabel magicAtkLabel;
    private JLabel defLabel;
    private JLabel energyLabel;
    private JLabel playerLabel;

    private JButton moveButton;
    private JButton attackButton;

    private Unit selectedUnit;
    private Board board;

    private JLabel portraitLabel;

    private Font pixelfont;

    public ActionPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setBackground(Color.DARK_GRAY);

        try {
            InputStream is = getClass().getResourceAsStream("/assets/PixelifySans-VariableFont_wght.ttf");

            if (is == null) {
                throw new RuntimeException("Font not found on classpath");
            }

            pixelfont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(16f);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            pixelfont = new Font("Serif", Font.BOLD, 16);
        }

        turnLabel = new JLabel("");
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setFont(pixelfont);

        JPanel turnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        turnPanel.setOpaque(false);
        turnPanel.add(turnLabel);

        add(turnPanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);

        nameLabel = new JLabel("No unit selected");
        hpLabel = new JLabel("HP: -");
        atkLabel = new JLabel("ATK: -");
        magicAtkLabel = new JLabel("MAGIC ATK: -");
        defLabel = new JLabel("DEF: -");
        energyLabel = new JLabel("Energy: -");

        nameLabel.setFont(pixelfont.deriveFont(20f));
        hpLabel.setFont(pixelfont.deriveFont(14f));

        for (JLabel l : new JLabel[]{
                nameLabel, hpLabel, atkLabel, magicAtkLabel, defLabel, energyLabel
        }) {
            l.setForeground(Color.WHITE);
            l.setFont(pixelfont);
        }

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row1.setOpaque(false);
        row1.add(nameLabel);
        row1.add(hpLabel);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row2.setOpaque(false);
        row2.add(atkLabel);
        row2.add(magicAtkLabel);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row3.setOpaque(false);
        row3.add(defLabel);
        row3.add(energyLabel);

        statsPanel.add(row1);
        statsPanel.add(Box.createVerticalStrut(6));
        statsPanel.add(row2);
        statsPanel.add(Box.createVerticalStrut(6));
        statsPanel.add(row3);
        statsPanel.add(Box.createVerticalStrut(10));

        playerLabel = new JLabel("");
        playerLabel.setForeground(Color.WHITE);
        playerLabel.setFont(pixelfont.deriveFont(Font.BOLD, 18f));
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);

        buttonsPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 5, 15)
        );

        moveButton = new JButton("MOVE");
        moveButton.setForeground(Color.WHITE);
        moveButton.setFont(pixelfont.deriveFont(24f));
        attackButton = new JButton("ATTACK");
        attackButton.setForeground(Color.WHITE);
        attackButton.setFont(pixelfont.deriveFont(24f));

        moveButton.setEnabled(false);
        attackButton.setEnabled(false);

        moveButton.setBackground(new Color(52, 90, 192, 255));
        attackButton.setBackground(new Color(225, 54, 94, 255));

        buttonsPanel.add(moveButton);
        buttonsPanel.add(attackButton);

        add(buttonsPanel, BorderLayout.EAST);

        portraitLabel = new JLabel();
        portraitLabel.setPreferredSize(new Dimension(96, 96));
        portraitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        portraitLabel.setVerticalAlignment(SwingConstants.CENTER);

        portraitLabel.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        );

        moveButton.addActionListener(e -> {
            if (selectedUnit == null || board == null) return;

            board.setActionMode(Board.ActionMode.MOVE);
            board.showMoveHighlightsFor(selectedUnit);
        });

        attackButton.addActionListener(e -> {
            if (selectedUnit == null || board == null) return;
            if (selectedUnit.hasActedThisTurn) return;
            if (selectedUnit.team != board.currentTurn) return;
            if (selectedUnit.actions == null || selectedUnit.actions.isEmpty()) return;

            JPopupMenu menu = new JPopupMenu();

            for (int i = 0; i < selectedUnit.actions.size(); i++) {
                Action a = selectedUnit.actions.get(i);

                String label;
                String desc;

                if (i == 0) {
                    label = "Basic: " + a.getName();
                    desc = selectedUnit.basicDesc;
                } else if (i == 1) {
                    label = "Skill: " + a.getName();
                    desc = selectedUnit.skillDesc;
                } else if (i == 2) {
                    label = "Ultimate: " + a.getName();
                    desc = selectedUnit.ultimateDesc;
                } else {
                    label = a.getName();
                    desc = "";
                }

                JMenuItem item = new JMenuItem(
                    label + " (" + a.getEnergyCost() + ")"
                );

                if (desc != null && !desc.isEmpty()) {
                    item.setToolTipText(
                        "<html>" + desc.replace("\n", "<br>") + "</html>"
                    );
                }

                item.setEnabled(selectedUnit.energy >= a.getEnergyCost());

                item.addActionListener(ev -> {
                    board.selectedAction = a;

                    Action.TargetType type = a.getTargetType();

                    if (type == Action.TargetType.ENEMY) {

                        board.setActionMode(Board.ActionMode.ATTACK);
                        board.showAttackHighlightsFor(selectedUnit);

                    } else if (type == Action.TargetType.ALLY) {

                        board.setActionMode(Board.ActionMode.ALLY_TARGET);
                        board.showAllyHighlightsFor(selectedUnit);

                    } else if (type == Action.TargetType.SELF) {

                        board.beginAction(selectedUnit, a.getName());

                        a.execute(
                            board,
                            selectedUnit,
                            selectedUnit.getX(),
                            selectedUnit.getY()
                        );

                        board.endAction();

                        selectedUnit.spendEnergy(a.getEnergyCost());
                        selectedUnit.hasActedThisTurn = true;

                        board.setActionMode(Board.ActionMode.NONE);

                        if (board.allCurrentTeamActed()) {
                            board.endTurn();
                        }

                    } else if (type == Action.TargetType.GLOBAL) {

                        board.beginAction(selectedUnit, a.getName());

                        a.execute(board, selectedUnit, -1, -1);

                        board.endAction();

                        selectedUnit.spendEnergy(a.getEnergyCost());
                        selectedUnit.hasActedThisTurn = true;

                        board.setActionMode(Board.ActionMode.NONE);

                        if (board.allCurrentTeamActed()) {
                            board.endTurn();
                        }
                    }
                });



                menu.add(item);
            }


            menu.show(attackButton, 0, attackButton.getHeight());
        });

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        contentPanel.add(portraitLabel);
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createHorizontalStrut(10));
        contentPanel.add(playerLabel);

        add(buttonsPanel, BorderLayout.EAST);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.EAST);
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setCurrentTurn(units.Unit.Team team) {
        String player = (team == units.Unit.Team.ALLY) ? "Player 1" : "Player 2";
        // turnLabel.setText("Turn: " + player + " (" + team + ")");
    }

    public void setSelectedUnit(Unit u) {
        this.selectedUnit = u;

        if (u == null) {
            nameLabel.setText("No unit selected");
            hpLabel.setText("HP: -");
            atkLabel.setText("ATK: -");
            magicAtkLabel.setText("MAGIC ATK: -");
            defLabel.setText("DEF: -");
            energyLabel.setText("Energy: -");

            portraitLabel.setIcon(null);
            playerLabel.setText("");

            moveButton.setEnabled(false);
            attackButton.setEnabled(false);
        } else {
            nameLabel.setText(u.name);
            hpLabel.setText("HP: " + u.curHp + " / " + u.maxHp);
            atkLabel.setText("ATK: " + u.atk);
            magicAtkLabel.setText("MAGIC ATK: " + u.magicAtk);
            defLabel.setText("DEF: " + u.def);
            energyLabel.setText("Energy: " + u.energy + " / " + u.maxEnergy);

            portraitLabel.setIcon(new ImageIcon(
                u.picSmall.getScaledInstance(96, 96, Image.SCALE_SMOOTH)
            ));

            String playerText =
                (u.team == Unit.Team.ALLY) ? "PLAYER 1" : "PLAYER 2";
            playerLabel.setText(playerText);

            moveButton.setEnabled(true);
            attackButton.setEnabled(true);
        }
    }


    public void clearSelection() {
        setSelectedUnit(null);
    }
}
