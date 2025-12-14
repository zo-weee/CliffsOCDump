package main;

import units.Unit;
import actions.Action;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Board extends JPanel {

    public enum ActionMode {
        NONE,
        MOVE,
        ATTACK
    }

    public Action selectedAction;

    public ActionMode currentMode = ActionMode.NONE;

    public int tileSize = 50;
    int cols = 12;
    int rows = 12;

    public int offsetX = 40;
    public int offsetY = 40;

    ArrayList<Unit> units;
    private List<Point> highlights = new ArrayList<>();

    public Unit selectedUnit;
    private ActionPanel actionPanel;

    // optional convenience ctor if old code uses it
    public Board(ArrayList<Unit> selectedUnits) {
        this(selectedUnits, null);
    }

    public Board(ArrayList<Unit> selectedUnits, ActionPanel actionPanel) {
        this.units = selectedUnits;
        this.actionPanel = actionPanel;

        int width = cols * tileSize + offsetX * 2;
        int height = rows * tileSize + offsetY * 2;
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);

        int[] startX = {3, 4, 5, 6};
        int startY = 10;

        for (int i = 0; i < units.size() && i < startX.length; i++) {
            units.get(i).setPosition(startX[i], startY);
        }

        if (this.actionPanel != null) {
            this.actionPanel.setBoard(this);
        }
    }

    // ==== Queries ====

    public Unit getUnit(int col, int row) {
        for (Unit u : this.units) {
            if (u.getX() == col && u.getY() == row) {
                return u;
            }
        }
        return null;
    }

    // ==== Move / Attack execution ====

    public void makeMove(Move move) {
        if (move.block != null) {
            this.units.remove(move.block);
        }
        move.u.setPosition(move.new_col, move.new_row);
        repaint();
    }

    public void performAttack(Unit attacker, int targetCol, int targetRow) {
        Unit target = getUnit(targetCol, targetRow);
        if (attacker.team == target.team) {
            System.out.println("Cannot attack ally.");
            return;
        }

        if (target == null || target == attacker) return;

        int rawDamage = attacker.atk;
        int finalDamage = Math.max(0, rawDamage - target.def);

        target.takeDamage(finalDamage);

        System.out.println(
            attacker.name + " hits " + target.name +
            " for " + finalDamage + " damage (HP left: " + target.curHp + ")"
        );

        if (!target.isAlive()) {
            units.remove(target);
            System.out.println(target.name + " was defeated.");
        }

        repaint();
    }


    // ==== Highlights & selection ====

    public void setHighlights(List<Point> tiles) {
        this.highlights = tiles;
        repaint();
    }

    public void clearHighlights() {
        this.highlights.clear();
        repaint();
    }

    public void setActionMode(ActionMode mode) {
        this.currentMode = mode;
        clearHighlights();
    }

    public void onUnitSelected(Unit u) {
        this.selectedUnit = u;
        if (actionPanel != null) {
            actionPanel.setSelectedUnit(u);
        }
    }

    public void clearSelection() {
        this.selectedUnit = null;
        this.currentMode = ActionMode.NONE;
        clearHighlights();
        if (actionPanel != null) {
            actionPanel.clearSelection();
        }
    }

    public void showMoveHighlightsFor(Unit unitXY) {
        List<Point> tiles = new ArrayList<>();
        int startCol = unitXY.getX();
        int startRow = unitXY.getY();
        int moveRange = unitXY.moveRange;

        for (int dx = -moveRange; dx <= moveRange; dx++) {
            for (int dy = -moveRange; dy <= moveRange; dy++) {
                int targetCol = startCol + dx;
                int targetRow = startRow + dy;

                if (targetCol < 0 || targetCol >= cols) continue;
                if (targetRow < 0 || targetRow >= rows) continue;

                if (ValidateMove.isMoveValid(this, unitXY, targetCol, targetRow)) {
                    tiles.add(new Point(targetCol, targetRow));
                }
            }
        }
        setHighlights(tiles);
    }

    public void showAttackHighlightsFor(Unit unitXY) {
        List<Point> tiles = new ArrayList<>();
        int startCol = unitXY.getX();
        int startRow = unitXY.getY();
        int range = unitXY.attackRange;

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                int targetCol = startCol + dx;
                int targetRow = startRow + dy;

                if (dx == 0 && dy == 0) continue; // can't attack self
                if (targetCol < 0 || targetCol >= cols) continue;
                if (targetRow < 0 || targetRow >= rows) continue;

                // only highlight tiles that actually have a unit to hit
                Unit target = getUnit(targetCol, targetRow);
                if (target != null && target.team != unitXY.team) {
                    tiles.add(new Point(targetCol, targetRow));
                }

            }
        }
        setHighlights(tiles);
    }

    // ==== Painting ====

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // board background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(offsetX, offsetY, cols * tileSize, rows * tileSize);

        // grid
        g2d.setColor(Color.BLACK);
        for (int r = 0; r <= rows; r++) {
            int y = offsetY + r * tileSize;
            g2d.drawLine(offsetX, y, offsetX + cols * tileSize, y);
        }
        for (int c = 0; c <= cols; c++) {
            int x = offsetX + c * tileSize;
            g2d.drawLine(x, offsetY, x, offsetY + rows * tileSize);
        }

        // highlights: blue for move, red for attack
        if (currentMode == ActionMode.MOVE) {
            g2d.setColor(new Color(0, 0, 255, 80));
        } else if (currentMode == ActionMode.ATTACK) {
            g2d.setColor(new Color(255, 0, 0, 80));
        } else {
            g2d.setColor(new Color(0, 0, 255, 80)); // default
        }

        for (Point p : highlights) {
            int x = offsetX + p.x * tileSize;
            int y = offsetY + p.y * tileSize;
            g2d.fillRect(x + 1, y + 1, tileSize - 2, tileSize - 2);
        }

        // units
        for (Unit u : units) {
            u.draw(g2d, tileSize, offsetX, offsetY);
        }
    }

    public void applyDamage(Unit target, int damage) {
        int finalDamage = Math.max(0, damage - target.def);
        target.takeDamage(finalDamage);

        if (!target.isAlive()) {
            units.remove(target);
        }

        repaint();
    }
}
