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
    private JLabel defLabel;
    private JLabel energyLabel;

    private JButton moveButton;
    private JButton attackButton;

    private Unit selectedUnit;
    private Board board;  // set from outside

    public ActionPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setBackground(Color.DARK_GRAY);

        turnLabel = new JLabel("Turn: Player 1 (ALLY)");
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel turnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        turnPanel.setOpaque(false);
        turnPanel.add(turnLabel);

        add(turnPanel, BorderLayout.NORTH);


        // === STATS AREA ===
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(0, 1));
        statsPanel.setOpaque(false);

        nameLabel = new JLabel("No unit selected");
        hpLabel   = new JLabel("HP: -");
        atkLabel  = new JLabel("ATK: -");
        defLabel  = new JLabel("DEF: -");
        energyLabel = new JLabel("Energy: -");

        for (JLabel l : new JLabel[]{nameLabel, hpLabel, atkLabel, defLabel, energyLabel}) {
            l.setForeground(Color.WHITE);
        }

        statsPanel.add(nameLabel);
        statsPanel.add(hpLabel);
        statsPanel.add(atkLabel);
        statsPanel.add(defLabel);
        statsPanel.add(energyLabel);

        // === ACTION BUTTONS ===
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);

        moveButton = new JButton("Move");
        attackButton = new JButton("Attack");

        moveButton.setEnabled(false);
        attackButton.setEnabled(false);

        buttonsPanel.add(moveButton);
        buttonsPanel.add(attackButton);

        add(statsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.WEST);

        // === BUTTON LOGIC ===

        moveButton.addActionListener(e -> {
            if (selectedUnit == null || board == null) return;

            board.setActionMode(Board.ActionMode.MOVE);
            board.showMoveHighlightsFor(selectedUnit);
        });


        attackButton.addActionListener(e -> {
            if (selectedUnit == null || board == null) return;
            if (selectedUnit.actions.isEmpty()) return;

            // ✅ STEP 2: select the unit's basic attack
            Action action = selectedUnit.actions.get(0);
            board.selectedAction = action;

            board.setActionMode(Board.ActionMode.ATTACK);
            board.showAttackHighlightsFor(selectedUnit);
        });
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setCurrentTurn(units.Unit.Team team) {
        String player = (team == units.Unit.Team.ALLY) ? "Player 1" : "Player 2";
        turnLabel.setText("Turn: " + player + " (" + team + ")");
    }


    public void setSelectedUnit(Unit u) {
        this.selectedUnit = u;

        if (u == null) {
            nameLabel.setText("No unit selected");
            hpLabel.setText("HP: -");
            atkLabel.setText("ATK: -");
            defLabel.setText("DEF: -");
            moveButton.setEnabled(false);
            attackButton.setEnabled(false);
        } else {
            nameLabel.setText(u.name);
            hpLabel.setText("HP: " + u.curHp + " / " + u.maxHp);
            atkLabel.setText("ATK: " + u.atk);
            defLabel.setText("DEF: " + u.def);
            moveButton.setEnabled(true);
            attackButton.setEnabled(true);
            energyLabel.setText("Energy: " + u.energy + " / " + u.maxEnergy);
        }
    }

    public void clearSelection() {
        setSelectedUnit(null);
    }
}
