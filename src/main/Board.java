package main;

import actions.Action;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.Timer;
import units.Mage;
import units.Unit;

public class Board extends JPanel {

    public enum ActionMode {
        NONE,
        MOVE,
        ATTACK
    }

    // ===== Turn / Hotseat =====
    public Unit.Team currentTurn = Unit.Team.ALLY;

    // ===== Action selection =====
    public Action selectedAction;
    public ActionMode currentMode = ActionMode.NONE;

    // ===== Board dimensions =====
    public int tileSize = 50;
    int cols = 10;
    int rows = 10;

    public int offsetX = 40;
    public int offsetY = 40;

    // ===== Damage tuning (fixes "0 dmg") =====
    private static final double DEF_SCALE = 1000.0; // matches your DEF magnitude (~hundreds)
    private static final int MIN_DAMAGE = 1;

    // ===== Units / UI =====
    private final ArrayList<Unit> units;
    public Unit selectedUnit;
    private final ActionPanel actionPanel;

    // ===== Highlights =====
    private List<Point> highlights = new ArrayList<>();
    private boolean attackHighlightsSupport = false;

    private StatusBoard statusBoard;

    // ===== StatusBoard action context =====
    private Unit logActor = null;
    private String logActionName = null;

    // ===== Anti-double-execute guard =====
    private boolean resolvingAction = false;

    public Board(ArrayList<Unit> selectedUnits) {
        this(selectedUnits, null);
    }

    public Board(ArrayList<Unit> selectedUnits, ActionPanel actionPanel) {
        this.units = (selectedUnits == null) ? new ArrayList<>() : new ArrayList<>(selectedUnits);
        this.actionPanel = actionPanel;

        dedupeUnitsByIdentity(); // ✅ fixes repeated logs if same object added twice

        int width = cols * tileSize + offsetX * 2;
        int height = rows * tileSize + offsetY * 2;
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);

        placeInitialUnits();

        if (!hasAnyEnemies()) {
            spawnDummyEnemies();
        }

        if (this.actionPanel != null) {
            this.actionPanel.setBoard(this);
            this.actionPanel.setCurrentTurn(currentTurn);
        }

        new Timer(16, e -> {
            for (Unit u : units) {
                u.updateAnimation();
            }
            repaint();
        }).start();
    }

    // ==============================
    // Setup
    // ==============================

    private void dedupeUnitsByIdentity() {
        Set<Unit> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        units.removeIf(u -> u == null || !seen.add(u));
    }

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
                if (u.team == null) u.team = Unit.Team.ALLY;

                if (allyIndex < startX.length) {
                    u.setPosition(startX[allyIndex], allyRow);
                    allyIndex++;
                }
            }

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
        for (Unit u : units) {
            if (u.getX() == col && u.getY() == row) return u;
        }
        return null;
    }

    private boolean isInsideBoard(int col, int row) {
        return col >= 0 && col < cols && row >= 0 && row < rows;
    }

    public boolean isClickable(Unit u) {
        return u != null
                && u.isAlive()
                && u.team == currentTurn
                && !u.hasActedThisTurn;
    }

    // ==============================
    // Move
    // ==============================

    public void makeMove(Move move) {
        logLine("[" + p(move.u.team) + "] " + move.u.name +
                " moves! (" + move.og_col + "," + move.og_row + " -> " +
                move.new_col + "," + move.new_row + ")");

        if (move.block != null) {
            units.remove(move.block);
        }

        move.u.setPosition(move.new_col, move.new_row);
        repaint();
    }

    // ==============================
    // Damage / Heal (fix "0 dmg")
    // ==============================

    private int mitigateDamage(int rawDamage, Unit target) {
        if (rawDamage <= 0 || target == null) return 0;

        int def = Math.max(0, target.def);
        double mult = DEF_SCALE / (DEF_SCALE + def); 
        int finalDamage = (int)Math.round(rawDamage * mult);

        if (finalDamage < MIN_DAMAGE) finalDamage = MIN_DAMAGE;
        return finalDamage;
    }

    public void applyDamage(Unit target, int rawDamage) {
        if (target == null || !target.isAlive()) return;

        int finalDamage = mitigateDamage(rawDamage, target);
        target.takeDamage(finalDamage);

        String targetLabel = unitLabel(target);
        String sourceLabel = (logActor != null) ? unitLabel(logActor) : "System";

        logLine("  - " + targetLabel + " takes " + finalDamage +
                " dmg (HP: " + target.curHp + "/" + target.maxHp + ")" +
                " (source: " + sourceLabel + ")");

        if (!target.isAlive()) {
            units.remove(target);
            logLine("    * " + targetLabel + " was defeated!");
        }
    }


    public void applyHeal(Unit target, int healAmount) {
        if (target == null || !target.isAlive()) return;
        if (healAmount <= 0) return;

        int before = target.curHp;
        target.curHp = Math.min(target.maxHp, target.curHp + healAmount);
        int healed = target.curHp - before;

        String targetLabel = unitLabel(target);
        String sourceLabel = (logActor != null) ? unitLabel(logActor) : "System";

        logLine("  - " + targetLabel + " healed +" + healed +
                " (HP: " + target.curHp + "/" + target.maxHp + ")" +
                " (source: " + sourceLabel + ")");
    }


    // ==============================
    // Target highlights (fix friendly targeting)
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
        clearHighlights();
    }

    public void onUnitSelected(Unit u) {
        this.selectedUnit = u;
        if (actionPanel != null) actionPanel.setSelectedUnit(u);
        repaint();
    }

    public void clearSelection() {
        this.selectedUnit = null;
        this.currentMode = ActionMode.NONE;
        this.selectedAction = null;
        this.attackHighlightsSupport = false;
        clearHighlights();
        if (actionPanel != null) actionPanel.clearSelection();
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

    // ✅ NEW: highlight based on action.canTarget (works for allies too)
    public void showActionTargetsFor(Unit user, Action action) {
        if (user == null || action == null) {
            attackHighlightsSupport = false;
            setHighlights(new ArrayList<>());
            return;
        }

        attackHighlightsSupport = action.isSupport();

        List<Point> tiles = new ArrayList<>();

        boolean canHitAllies = false;
        boolean canHitEnemies = false;

        for (Unit t : units) {
            if (t == null || !t.isAlive()) continue;
            int c = t.getX();
            int r = t.getY();
            if (action.canTarget(this, user, c, r)) {
                tiles.add(new Point(c, r));
                if (t.team == user.team) canHitAllies = true;
                else canHitEnemies = true;
            }
        }

        // If action can target both teams (usually "global-ish"), highlight by support/offense
        if (canHitAllies && canHitEnemies) {
            tiles.clear();
            for (Unit t : units) {
                if (t == null || !t.isAlive()) continue;
                if (attackHighlightsSupport) {
                    if (t.team == user.team) tiles.add(new Point(t.getX(), t.getY()));
                } else {
                    if (t.team != user.team) tiles.add(new Point(t.getX(), t.getY()));
                }
            }
        }

        setHighlights(tiles);
    }

    // ==============================
    // Execute selected action (fix "skills don't end turn" + double fires)
    // ==============================

    public boolean tryExecuteSelectedActionAt(int col, int row) {
        if (resolvingAction) return false;

        if (currentMode != ActionMode.ATTACK) return false;
        if (selectedUnit == null || selectedAction == null) return false;

        Unit u = selectedUnit;
        Action a = selectedAction;

        // hotseat rules
        if (u.team != currentTurn) return false;
        if (u.hasActedThisTurn) return false;

        // energy check
        int cost = a.getEnergyCost();
        if (!u.hasEnoughEnergy(cost)) return false;

        // targeting check
        if (!a.canTarget(this, u, col, row)) return false;

        resolvingAction = true;
        try {
            beginAction(u, a.getName());
            try {
                a.execute(this, u, col, row);
            } finally {
                endAction();
            }

            u.spendEnergy(cost);
            u.hasActedThisTurn = true;

            // clear attack mode (no rework; just reset)
            selectedAction = null;
            setActionMode(ActionMode.NONE);

            // refresh panel stats (energy, etc.)
            if (actionPanel != null) actionPanel.setSelectedUnit(u);

            repaint();

            if (allCurrentTeamActed()) {
                endTurn();
            }

            return true;
        } finally {
            resolvingAction = false;
        }
    }

    // ==============================
    // Turn logic
    // ==============================

    public boolean allCurrentTeamActed() {
        for (Unit u : units) {
            if (u.team == currentTurn && u.isAlive() && !u.hasActedThisTurn) return false;
        }
        return true;
    }

    public void endTurn() {
        for (Unit u : units) {
            if (u.team == currentTurn && u.isAlive()) {
                u.processStatuses(this);
            }
        }

        currentTurn = (currentTurn == Unit.Team.ALLY) ? Unit.Team.ENEMY : Unit.Team.ALLY;

        for (Unit u : units) {
            if (u.team == currentTurn && u.isAlive()) {
                u.resetTurn();
            }
        }

        clearSelection();

        if (actionPanel != null) {
            actionPanel.setCurrentTurn(currentTurn);
        }

        repaint();
    }

    // ==============================
    // StatusBoard logging
    // ==============================

    public void setStatusBoard(StatusBoard sb) {
        this.statusBoard = sb;
    }

    private String unitLabel(Unit u) {
        if (u == null) return "Unknown";
        String owner = (u.team == null) ? "?" : p(u.team);
        return owner + "'s " + u.name;
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

        g2d.setColor(Color.WHITE);
        g2d.fillRect(offsetX, offsetY, cols * tileSize, rows * tileSize);

        g2d.setColor(Color.BLACK);
        for (int r = 0; r <= rows; r++) {
            int y = offsetY + r * tileSize;
            g2d.drawLine(offsetX, y, offsetX + cols * tileSize, y);
        }
        for (int c = 0; c <= cols; c++) {
            int x = offsetX + c * tileSize;
            g2d.drawLine(x, offsetY, x, offsetY + rows * tileSize);
        }

        // clickable units green (only in NONE mode)
        if (currentMode == ActionMode.NONE) {
            for (Unit u : units) {
                if (isClickable(u)) {
                    int x = offsetX + u.getX() * tileSize;
                    int y = offsetY + u.getY() * tileSize;

                    g2d.setColor(new Color(0, 255, 0, 70));
                    g2d.fillRect(x + 1, y + 1, tileSize - 2, tileSize - 2);
                }
            }
        }

        // highlights color
        if (currentMode == ActionMode.MOVE) {
            g2d.setColor(new Color(0, 0, 255, 80));
        } else if (currentMode == ActionMode.ATTACK) {
            g2d.setColor(attackHighlightsSupport
                    ? new Color(0, 255, 0, 80)
                    : new Color(255, 0, 0, 80));
        } else {
            g2d.setColor(new Color(0, 0, 255, 80));
        }

        for (Point p : highlights) {
            int x = offsetX + p.x * tileSize;
            int y = offsetY + p.y * tileSize;
            g2d.fillRect(x + 1, y + 1, tileSize - 2, tileSize - 2);
        }

        for (Unit u : units) {
            u.draw(g2d, tileSize, offsetX, offsetY);
        }

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

    

    public ArrayList<Unit> getUnits() {
        return units;
    }
}
