package main;

import actions.Action;
import java.awt.*;
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

    private JButton moveButton;
    private JButton attackButton;

    private Unit selectedUnit;
    private Board board;

    private JLabel portraitLabel;

    public ActionPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setBackground(Color.DARK_GRAY);

        /* ================= TURN LABEL ================= */

        turnLabel = new JLabel("");
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel turnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        turnPanel.setOpaque(false);
        turnPanel.add(turnLabel);

        add(turnPanel, BorderLayout.NORTH);

        /* ================= STATS PANEL ================= */

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);

        nameLabel = new JLabel("No unit selected");
        hpLabel = new JLabel("HP: -");
        atkLabel = new JLabel("ATK: -");
        magicAtkLabel = new JLabel("MAGIC ATK: -");
        defLabel = new JLabel("DEF: -");
        energyLabel = new JLabel("Energy: -");

        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        hpLabel.setFont(new Font("Arial", Font.BOLD, 14));

        for (JLabel l : new JLabel[]{
                nameLabel, hpLabel, atkLabel, magicAtkLabel, defLabel, energyLabel
        }) {
            l.setForeground(Color.WHITE);
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

        /* ================= BUTTONS ================= */

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);

        buttonsPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 5, 15)
        );

        moveButton = new JButton("Move");
        attackButton = new JButton("Attack");

        moveButton.setEnabled(false);
        attackButton.setEnabled(false);

        buttonsPanel.add(moveButton);
        buttonsPanel.add(attackButton);

        /* ================= PORTRAIT ================= */

        portraitLabel = new JLabel();
        portraitLabel.setPreferredSize(new Dimension(96, 96));
        portraitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        portraitLabel.setVerticalAlignment(SwingConstants.CENTER);

        portraitLabel.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        );

        /* ================= BUTTON LOGIC ================= */

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

                String prefix;
                if (i == 0) prefix = "Basic: ";
                else if (i == 1) prefix = "Skill: ";
                else if (i == 2) prefix = "Ultimate: ";
                else prefix = "";

                JMenuItem item = new JMenuItem(
                        prefix + a.getName() + " (" + a.getEnergyCost() + ")"
                );

                item.setEnabled(selectedUnit.energy >= a.getEnergyCost());

                item.addActionListener(ev -> {
                    board.selectedAction = a;
                    board.setActionMode(Board.ActionMode.ATTACK);
                    board.showActionTargetsFor(selectedUnit, a);

                });

                menu.add(item);
            }

            menu.show(attackButton, 0, attackButton.getHeight());
        });

        /* ================= LAYOUT: CENTER + EAST ================= */

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        contentPanel.add(portraitLabel);
        contentPanel.add(statsPanel);

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

            moveButton.setEnabled(true);
            attackButton.setEnabled(true);
        }
    }

    public void clearSelection() {
        setSelectedUnit(null);
    }
}
