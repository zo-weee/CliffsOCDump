package main;

import actions.Action;
import units.Unit;
import units.Mage;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

public class Board extends JPanel {

    public enum ActionMode {
        NONE,
        MOVE,
        ATTACK
    }

    // ===== Turn / Hotseat =====
    public Unit.Team currentTurn = Unit.Team.ALLY; // Player 1 (ALLY) starts

    // ===== Action selection =====
    public Action selectedAction;
    public ActionMode currentMode = ActionMode.NONE;

    // ===== Board dimensions =====
    public int tileSize = 50;
    int cols = 10;
    int rows = 10;

    public int offsetX = 40;
    public int offsetY = 40;

    // ===== Units / UI =====
    ArrayList<Unit> units;
    public Unit selectedUnit;
    private ActionPanel actionPanel;

    // ===== Highlights =====
    private List<Point> highlights = new ArrayList<>();

    private StatusBoard statusBoard;

    // current “action context” so applyDamage/applyHeal can print who caused it
    private Unit logActor = null;
    private String logActionName = null;

    // optional convenience ctor if old code uses Board(selectedUnits)
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

        // Place ALLY and ENEMY units in different rows (simple spawn)
        placeInitialUnits();

        // TEMP: If you don't pass enemies in yet, you can spawn dummy enemies.
        // Comment this out once you have real Player 2 selection.
        if (!hasAnyEnemies()) {
            spawnDummyEnemies();
        }

        if (this.actionPanel != null) {
            this.actionPanel.setBoard(this);
            this.actionPanel.setCurrentTurn(currentTurn); // show initial turn
        }

        // Animation timer (~60 FPS)
        new Timer(16, e -> {
            for (Unit u : units) {
                u.updateAnimation();
            }
            repaint();
        }).start();
    }

    // ==============================
    // Spawn / Setup
    // ==============================

    private void placeInitialUnits() {
        int[] startX = {3, 4, 5, 6};

        int allyRow = rows - 2;
        int enemyRow = 1;

        int allyIndex = 0;
        int enemyIndex = 0;

        for (Unit u : units) {
            if (u.team == Unit.Team.ENEMY) {
                if (enemyIndex < startX.length) {
                    u.setPosition(startX[enemyIndex], enemyRow);
                    enemyIndex++;
                }
            } else {
                // default to ALLY if unset
                if (u.team == null) u.team = Unit.Team.ALLY;

                if (allyIndex < startX.length) {
                    u.setPosition(startX[allyIndex], allyRow);
                    allyIndex++;
                }
            }

            // new game: ensure units haven't acted yet
            u.hasActedThisTurn = false;
        }
    }

    private boolean hasAnyEnemies() {
        for (Unit u : units) {
            if (u.team == Unit.Team.ENEMY) return true;
            }
            return false;
        }

        private void spawnDummyEnemies() {
        Mage e1 = new Mage(0, 0);
        Mage e2 = new Mage(0, 0);
        Mage e3 = new Mage(0, 0);

        e1.name = "Dummy Enemy 1";
        e2.name = "Dummy Enemy 2";
        e3.name = "Dummy Enemy 3";

        e1.team = Unit.Team.ENEMY;
        e2.team = Unit.Team.ENEMY;
        e3.team = Unit.Team.ENEMY;

        units.add(e1);
        units.add(e2);
        units.add(e3);

        placeInitialUnits();
    }

    // ==============================
    // Queries
    // ==============================

    public Unit getUnit(int col, int row) {
        for (Unit u : this.units) {
            if (u.getX() == col && u.getY() == row) {
                return u;
            }
        }
        return null;
    }

    private boolean isInsideBoard(int col, int row) {
        return col >= 0 && col < cols && row >= 0 && row < rows;
    }

    // Can the current player click/select this unit right now?
    public boolean isClickable(Unit u) {
        return u != null
                && u.isAlive()
                && u.team == currentTurn
                && !u.hasActedThisTurn;
    }

    // ==============================
    // Move / Attack execution
    // ==============================

    public void makeMove(Move move) {
        logLine("[" + p(move.u.team) + "] " + move.u.name +
                " moves! (" + move.og_col + "," + move.og_row + " -> " +
                move.new_col + "," + move.new_row + ")");

        if (move.block != null) {
            this.units.remove(move.block);
        }

        move.u.setPosition(move.new_col, move.new_row);
        repaint();
    }


    public void performAttack(Unit attacker, int targetCol, int targetRow) {
        Unit target = getUnit(targetCol, targetRow);

        // ✅ prevent NullPointer + self-hit
        if (target == null || target == attacker) return;

        if (attacker.team == target.team) {
            System.out.println("Cannot attack ally.");
            return;
        }

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

    public void applyDamage(Unit target, int damage) {
        if (target == null) return;

        int before = target.curHp;
        int finalDamage = Math.max(0, damage - target.def);

        target.takeDamage(finalDamage);

        // Indented effect line
        String src = (logActor != null) ? ("[" + p(logActor.team) + "] " + logActor.name) : "[System]";
        logLine("  - " + target.name + " takes " + finalDamage +
                " dmg (HP: " + target.curHp + "/" + target.maxHp + ")  <" + src + ">");

        if (!target.isAlive()) {
            units.remove(target);
            logLine("    * " + target.name + " was defeated!");
        }

        repaint();
    }

    public void applyHeal(Unit target, int healAmount) {
        if (target == null) return;

        int before = target.curHp;
        target.curHp = Math.min(target.maxHp, target.curHp + healAmount);
        int healed = target.curHp - before;

        String src = (logActor != null) ? ("[" + p(logActor.team) + "] " + logActor.name) : "[System]";
        logLine("  - " + target.name + " healed +" + healed +
                " (HP: " + target.curHp + "/" + target.maxHp + ")  <" + src + ">");

        repaint();
    }



    // ==============================
    // Highlights / selection / modes
    // ==============================

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
        clearHighlights(); // always clear old highlights when mode changes
    }

    public void onUnitSelected(Unit u) {
        this.selectedUnit = u;
        if (actionPanel != null) {
            actionPanel.setSelectedUnit(u);
        }
        repaint();
    }

    public void clearSelection() {
        this.selectedUnit = null;
        this.currentMode = ActionMode.NONE;
        this.selectedAction = null;
        clearHighlights();
        if (actionPanel != null) {
            actionPanel.clearSelection();
        }
        repaint();
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

                if (!isInsideBoard(targetCol, targetRow)) continue;

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

                if (dx == 0 && dy == 0) continue;
                if (!isInsideBoard(targetCol, targetRow)) continue;

                Unit target = getUnit(targetCol, targetRow);
                if (target != null && target.team != unitXY.team) {
                    tiles.add(new Point(targetCol, targetRow));
                }
            }
        }

        setHighlights(tiles);
    }

    // ==============================
    // Turn logic (hotseat)
    // ==============================

    public boolean allCurrentTeamActed() {
        for (Unit u : units) {
            if (u.team == currentTurn && u.isAlive() && !u.hasActedThisTurn) {
                return false;
            }
        }
        return true;
    }

    public void endTurn() {
        // process statuses only for the team that just finished
        for (Unit u : units) {
            if (u.team == currentTurn && u.isAlive()) {
                u.processStatuses(this);
            }
        }

        // swap team
        currentTurn = (currentTurn == Unit.Team.ALLY) ? Unit.Team.ENEMY : Unit.Team.ALLY;

        // reset only the incoming team's "acted" flags
        for (Unit u : units) {
            if (u.team == currentTurn && u.isAlive()) {
                u.resetTurn(); // sets hasActedThisTurn = false
            }
        }

        // clear selection & UI
        clearSelection();

        System.out.println("=== " + currentTurn + " TURN ===");
        if (actionPanel != null) {
            actionPanel.setCurrentTurn(currentTurn);
        }

        repaint();
    }

    public void setStatusBoard(StatusBoard sb) {
        this.statusBoard = sb;
    }

    private String p(Unit.Team team) {
        return (team == Unit.Team.ALLY) ? "P1" : "P2";
    }

    private void logLine(String msg) {
        if (statusBoard != null) statusBoard.log(msg);
    }

    public void beginAction(Unit actor, String actionName) {
        this.logActor = actor;
        this.logActionName = actionName;
        logLine("[" + p(actor.team) + "] " + actor.name + " performs " + actionName + "!");
    }

    public void endAction() {
        this.logActor = null;
        this.logActionName = null;
    }

    // ==============================
    // Painting
    // ==============================

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // board background (white square)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(offsetX, offsetY, cols * tileSize, rows * tileSize);

        // draw turn text in black margin
        g2d.setColor(Color.WHITE);
        String player = (currentTurn == Unit.Team.ALLY) ? "Player 1" : "Player 2";
        g2d.drawString("Turn: " + player + " (" + currentTurn + ")", 10, 20);

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

        // ✅ Highlight clickable units (green) ONLY when not targeting a tile
        if (currentMode == ActionMode.NONE) {
            for (Unit u : units) {
                if (isClickable(u)) {
                    int x = offsetX + u.getX() * tileSize;
                    int y = offsetY + u.getY() * tileSize;

                    g2d.setColor(new Color(0, 255, 0, 70)); // translucent green
                    g2d.fillRect(x + 1, y + 1, tileSize - 2, tileSize - 2);
                }
            }
        }

        // move/attack highlight tiles
        if (currentMode == ActionMode.MOVE) {
            g2d.setColor(new Color(0, 0, 255, 80));
        } else if (currentMode == ActionMode.ATTACK) {
            g2d.setColor(new Color(255, 0, 0, 80));
        } else {
            g2d.setColor(new Color(0, 0, 255, 80));
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

        // ✅ Selected unit border (yellow)
        if (selectedUnit != null) {
            int x = offsetX + selectedUnit.getX() * tileSize;
            int y = offsetY + selectedUnit.getY() * tileSize;

            Stroke old = g2d.getStroke();
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(x + 2, y + 2, tileSize - 4, tileSize - 4);
            g2d.setStroke(old);
        }
    }

    // ==============================
    // Misc
    // ==============================

    public ArrayList<Unit> getUnits() {
        return units;
    }
}
