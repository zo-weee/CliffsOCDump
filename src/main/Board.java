package main;

import actions.Action;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import units.Mage;
import units.Unit;

public class Board extends JPanel {

    private Image[] passableTiles;
    private Image tileBlocked;
    private Image[][] tileImages;
    private boolean[][] terrain;

    private Random rng = new Random();

    public enum ActionMode {
        NONE,
        MOVE,
        ATTACK,
        ALLY_TARGET
    }

    public enum TurnPhase {
        START,
        END
    }

    private List<ScheduledAction> scheduledActions = new ArrayList<>();

    private String logActionName = null;
    public Unit.Team currentTurn = Unit.Team.ALLY;

    public Action selectedAction;
    public ActionMode currentMode = ActionMode.NONE;

    public int tileSize = 50;
    int cols = 10;
    int rows = 10;

    public int offsetX = 40;
    public int offsetY = 40;

    ArrayList<Unit> units;
    public Unit selectedUnit;
    private ActionPanel actionPanel;

    // 08-FEB-2026 NEW ADDITION: movement-related variables in Board
    // the idea is that the logical part of any animation-related stuff will be handled by other classes in main;
    // Board.java is only responsible for painting the results of the calculations done
    // in this one, ally and enemy movement is handled by Move.java and reflected by Board.java
    private Queue<Move> moveQueue = new ArrayDeque<>();
    private Move currentMove = null;

    private List<Point> highlights = new ArrayList<>();

    private StatusBoard statusBoard;
    private Unit logActor = null;

    private boolean gameOver = false;

    public interface TurnAction {
        void execute(Board board);
    }

    public interface GameOverListener {
        // winner == null means draw (both dead)
        void onGameOver(Unit.Team winner);
    }

    private GameOverListener gameOverListener;

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOverListener(GameOverListener l) {
        this.gameOverListener = l;
    }

    public Board(ArrayList<Unit> selectedUnits) {
        this(selectedUnits, null, EnvironmentType.GRASS);
    }

    private void loadEnvironment(EnvironmentType env) {
        switch (env) {
            case GRASS:
                passableTiles = new Image[]{
                    new ImageIcon(getClass().getResource("/assets/Environment/env1/pass/grass2.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env1/pass/grass3.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env1/pass/grass4.png")).getImage()
                };

                tileBlocked = new ImageIcon(
                    getClass().getResource("/assets/Environment/env1/block/grass_rock2.png")
                ).getImage();
                break;

            case CASTLE:
                passableTiles = new Image[]{
                    new ImageIcon(getClass().getResource("/assets/Environment/env2/pass/castle1.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env2/pass/castle2.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env2/pass/castle3.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env2/pass/castle4.png")).getImage()
                };

                tileBlocked = new ImageIcon(
                    getClass().getResource("/assets/Environment/env2/block/castle_column2.png")
                ).getImage();
                break;

            case GALAXY:
                passableTiles = new Image[]{
                    new ImageIcon(getClass().getResource("/assets/Environment/env3/pass/star1.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env3/pass/star2.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env3/pass/star3.png")).getImage(),
                    new ImageIcon(getClass().getResource("/assets/Environment/env3/pass/star4.png")).getImage()
                };

                tileBlocked = new ImageIcon(
                    getClass().getResource("/assets/Environment/env3/block/star_crystal1.png")
                ).getImage();
                break;
        }
    }

    public Board(ArrayList<Unit> selectedUnits, ActionPanel actionPanel, EnvironmentType environment) {

        this.units = selectedUnits;
        this.actionPanel = actionPanel;

        int width = cols * tileSize + offsetX * 2;
        int height = rows * tileSize + offsetY * 2;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);

        placeInitialUnits();
        loadEnvironment(environment);

        terrain = new boolean[rows][cols];
        tileImages = new Image[rows][cols];

        double BASE_BLOCK_CHANCE = 0.08;
        double NEAR_BLOCK_BONUS = 0.05;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                terrain[r][c] = rng.nextDouble() >= BASE_BLOCK_CHANCE;
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (terrain[r][c] && hasBlockedNeighbor(r, c)) {
                    if (rng.nextDouble() < NEAR_BLOCK_BONUS) {
                        terrain[r][c] = false;
                    }
                }
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tileImages[r][c] = terrain[r][c]
                    ? passableTiles[rng.nextInt(passableTiles.length)]
                    : tileBlocked;
            }
        }

        clearSpawnRows();

        if (!hasAnyEnemies()) {
            spawnDummyEnemies();
        }

        if (actionPanel != null) {
            actionPanel.setBoard(this);
            actionPanel.setCurrentTurn(currentTurn);
        }

        // 08-FEB-2026 NEW ADDITION: rebuilt timer system to instead gain animation queues from a Move.java queue
        // this basically lets us do a lot of funky animation-related stuff now
        new Timer(16, e -> {
            if (gameOver) return;
            if (currentMove != null) {
                currentMove.update(0.016f);
                if (currentMove.finished) {
                    currentMove = null;
                }
            }
            if (currentMove == null && !moveQueue.isEmpty()) {
                currentMove = moveQueue.poll();
            }
            for (Unit u : units) u.updateAnimation();
            repaint();
        }).start();
    }


    private String unitLabel(Unit u) {
        if (u == null) return "Unknown";
        String owner = (u.team == null) ? "?" : p(u.team);
        return owner + "'s " + u.name;
    }

    private void clearSpawnRows() {
        int allyRow = rows - 2;
        int enemyRow = 1;

        for (int c = 0; c < cols; c++) {
            terrain[allyRow][c] = true;
            terrain[enemyRow][c] = true;

            tileImages[allyRow][c] =
                passableTiles[rng.nextInt(passableTiles.length)];
            tileImages[enemyRow][c] =
                passableTiles[rng.nextInt(passableTiles.length)];
        }
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
                    Point.Float pixelPos = tileToPixel(u.getX(), u.getY());
                    u.pixelX = pixelPos.x;
                    u.pixelY = pixelPos.y;
                    enemyIndex++;
                }
            } else {
                if (u.team == null) u.team = Unit.Team.ALLY;

                if (allyIndex < startX.length) {
                    u.setPosition(startX[allyIndex], allyRow);
                    Point.Float pixelPos = tileToPixel(u.getX(), u.getY());
                    u.pixelX = pixelPos.x;
                    u.pixelY = pixelPos.y;
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

    private boolean hasBlockedNeighbor(int r, int c) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;

                int nr = r + dr;
                int nc = c + dc;

                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                    if (!terrain[nr][nc]) return true;
                }
            }
        }
        return false;
    }

    public Unit getUnit(int col, int row) {
        for (Unit u : this.units) {
            if (u.getX() == col && u.getY() == row) {
                return u;
            }
        }
        return null;
    }

    public boolean isTileBlocked(int col, int row) {
        return !terrain[row][col];
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
    /*
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
    */

    public void performAttack(Unit attacker, int targetCol, int targetRow) {
        Unit target = getUnit(targetCol, targetRow);

        if (target == null || target == attacker) return;

        if (attacker.team == target.team) {
            System.out.println("Cannot attack ally.");
            return;
        }

        double damageMultiplier = attacker.atk / (double)(attacker.atk + target.def);
        int finalDamage = (int)(attacker.atk * damageMultiplier);
        finalDamage = Math.max(1, finalDamage);


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

    // 14-JAN-2026 NEW ADDITION: this code schedules actions for when a move or thing occurs at the beginning or end of a turn,
    // whether it be the current or the next or whatever
    public void scheduleAction(int turns, TurnPhase phase, TurnAction action) {
        scheduledActions.add(new ScheduledAction(turns, phase, action));
    }

    // 14-JAN-2026 NEW ADDITION: this code resolves all the scheduled actions from the function above
    // the way this works is by checking whether the phase the action will happen is either START (start of turn) or END (end of turn)
    // if the actions scheduled have that phase and have the amount of turns they're delayed for by equal to or less than 0
    // execute them and remove them from the list
    private void resolveActions(TurnPhase phase) {
        Iterator<ScheduledAction> it = scheduledActions.iterator();

        while(it.hasNext()) {
            ScheduledAction act = it.next();
            if (act.phase != phase) continue;
            act.turnsRemaining--;
            if (act.turnsRemaining <= 0) {
                act.action.execute(this);
                it.remove();
            }
        }
    }

    // the capabilities of both functions above have been added to EndTurn()

    public void applyDamage(Unit target, int damage) {
        if (target == null) return;

        int finalDamage = Math.max(0, damage - target.def);

        target.takeDamage(finalDamage);
        target.triggerFlash(new Color(255, 0, 0, 140)); // RED
        target.hpRatio = target.curHp / (float) target.maxHp;

        String targetLabel = unitLabel(target);

        logLine("  - " + targetLabel + " takes " + finalDamage +
            " dmg (HP: " + target.curHp + "/" + target.maxHp + ")");

        if (!target.isAlive()) {
            units.remove(target);
            logLine("    * " + targetLabel + " was defeated!");
            checkGameOver();
        }

        repaint();
    }

    public void applyHeal(Unit target, int healAmount) {
        if (target == null) return;

        int before = target.curHp;
        target.takeHealing(healAmount);
        int healed = target.curHp - before;
        target.hpRatio = target.curHp / (float) target.maxHp;

        String targetLabel = unitLabel(target);
        target.triggerFlash(new Color(0, 255, 0, 140)); // GREEN

        logLine("  - " + targetLabel + " healed +" + healed +
                " (HP: " + target.curHp + "/" + target.maxHp + ")");

        repaint();
    }

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

        // basically this code goes crazy on the Discrete II
        // it really ate up all that DSA studying 
        List<Point> tiles = new ArrayList<>();

        int startCol = unitXY.getX();
        int startRow = unitXY.getY();
        int moveRange = unitXY.moveRange;

        boolean[][] visited = new boolean[rows][cols];
        int[][] remaining = new int[rows][cols];

        Queue<Point> queue = new ArrayDeque<>();

        queue.add(new Point(startCol, startRow));
        remaining[startRow][startCol] = moveRange;
        visited[startRow][startCol] = true;

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int col = p.x;
            int row = p.y;

            int movesLeft = remaining[row][col];

            if (!(col == startCol && row == startRow)) {
                if (getUnit(col, row) == null) {
                    tiles.add(p);
                }
            }

            if (movesLeft == 0) continue;

            int[][] dirs = {
                { 1, 0 }, { -1, 0 },
                { 0, 1 }, { 0, -1 }
            };

            for (int[] d : dirs) {
                int nc = col + d[0];
                int nr = row + d[1];

                if (!isInsideBoard(nc, nr)) continue;
                if (isTileBlocked(nc, nr)) continue;

                if (!visited[nr][nc] || remaining[nr][nc] < movesLeft - 1) {
                    visited[nr][nc] = true;
                    remaining[nr][nc] = movesLeft - 1;
                    queue.add(new Point(nc, nr));
                }
            }
        }

        setHighlights(tiles);
    }

    // 08-FEB-2026 NEW ADDITION: helps calculate the shortest path between the unit's tile and the target movement tile
    public void calculateShortestPath(Unit u, int targetCol, int targetRow, boolean ignoreAllies) {
        if (u == null) return;
        if (!isInsideBoard(targetCol, targetRow)) return;
        if (isTileBlocked(targetCol, targetRow)) return;

        boolean[][] visited = new boolean[rows][cols];
        Point[][] cameFrom = new Point[rows][cols];

        Queue<Point> queue = new ArrayDeque<>();
        Point start = new Point(u.getX(), u.getY());
        queue.add(start);
        visited[start.y][start.x] = true;

        int[][] directions = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        boolean found = false;

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (current.x == targetCol && current.y == targetRow) {
                found = true;
                break;
            }

            for (int[] d : directions) {
                int nx = current.x + d[0];
                int ny = current.y + d[1];

                if (!isInsideBoard(nx, ny)) continue;
                if (isTileBlocked(nx, ny)) continue;

                Unit blocker = getUnit(nx, ny);
                if (blocker != null && blocker != u) {
                    if (!ignoreAllies || blocker.team != u.team) {
                        continue; // only block if not ignoring allies
                    }
                } // can't pass through other units

                if (!visited[ny][nx]) {
                    visited[ny][nx] = true;
                    cameFrom[ny][nx] = current;
                    queue.add(new Point(nx, ny));
                }
            }
        }

        if (!found) {
            u.movePath = null; // no valid path
            return;
        }

        // backtrack to build path
        ArrayDeque<Point> path = new ArrayDeque<>();
        Point current = new Point(targetCol, targetRow);
        while (!current.equals(start)) {
            path.addFirst(current);
            current = cameFrom[current.y][current.x];
        }

        u.movePath = path;
    }

    public void showAttackHighlightsFor(Unit unitXY) {
        // watch me be the goat casually /j
        List<Point> tiles = new ArrayList<>();

        int startCol = unitXY.getX();
        int startRow = unitXY.getY();
        int moveRange = unitXY.attackRange;

        boolean[][] visited = new boolean[rows][cols];
        int[][] remaining = new int[rows][cols];

        Queue<Point> queue = new ArrayDeque<>();

        queue.add(new Point(startCol, startRow));
        remaining[startRow][startCol] = moveRange;
        visited[startRow][startCol] = true;

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int col = p.x;
            int row = p.y;

            int movesLeft = remaining[row][col];

            if (!(col == startCol && row == startRow)) {
                Unit target = getUnit(col, row);
                if (target != null && target.team != unitXY.team) {
                    tiles.add(p);
                }
            }

            if (movesLeft == 0) continue;

            int[][] dirs = {
                { 1, 0 }, { -1, 0 },
                { 0, 1 }, { 0, -1 }
            };

            for (int[] d : dirs) {
                int nc = col + d[0];
                int nr = row + d[1];

                if (!isInsideBoard(nc, nr)) continue;
                if (isTileBlocked(nc, nr)) continue;
                Unit blocker = getUnit(nc, nr);
                if (blocker != null) {
                    if (blocker.team != unitXY.team) {
                        if (!visited[nr][nc]) {
                            visited[nr][nc] = true;
                            remaining[nr][nc] = movesLeft - 1;
                            queue.add(new Point(nc, nr));
                        }
                    }
                    continue;
                }

                if (!visited[nr][nc] || remaining[nr][nc] < movesLeft - 1) {
                    visited[nr][nc] = true;
                    remaining[nr][nc] = movesLeft - 1;
                    queue.add(new Point(nc, nr));
                }
            }
        }

        setHighlights(tiles);
    }

    public boolean allCurrentTeamActed() {
        for (Unit u : units) {
            if (u.team == currentTurn && u.isAlive() && !u.hasActedThisTurn) {
                return false;
            }
        }
        return true;
    }

    public void endTurn() {

        resolveActions(TurnPhase.END);

        for (Unit u : units) {
            if (u.team == currentTurn && u.isAlive()) {
                u.processStatuses(this);
            }
        }

        currentTurn = (currentTurn == Unit.Team.ALLY) ? Unit.Team.ENEMY : Unit.Team.ALLY;

        resolveActions(TurnPhase.START);

        for (Unit u : units) {
            if (u.team == currentTurn && u.isAlive()) {
                u.resetTurn();
            }
        }

        clearSelection();

        System.out.println("=== " + currentTurn + " TURN ===");
        if (actionPanel != null) {
            actionPanel.setCurrentTurn(currentTurn);
        }

        checkGameOver();
        repaint();
    }

    private void checkGameOver() {
        boolean allyAlive = false;
        boolean enemyAlive = false;

        for (Unit u : units) {
            if (u == null || !u.isAlive()) continue;
            if (u.team == Unit.Team.ALLY) allyAlive = true;
            if (u.team == Unit.Team.ENEMY) enemyAlive = true;
        }

        if (!allyAlive || !enemyAlive) {
            Unit.Team winner;
            if (allyAlive && !enemyAlive) winner = Unit.Team.ALLY;
            else if (!allyAlive && enemyAlive) winner = Unit.Team.ENEMY;
            else winner = null; 

            triggerGameOver(winner);
        }
    }

    private void triggerGameOver(Unit.Team winner) {
        if (gameOver) return;   
        gameOver = true;

        clearSelection();
        clearHighlights();
        selectedAction = null;
        currentMode = ActionMode.NONE;

        String msg = (winner == Unit.Team.ALLY) ? "=== GAME OVER: PLAYER 1 WINS ==="
                : (winner == Unit.Team.ENEMY) ? "=== GAME OVER: PLAYER 2 WINS ==="
                : "=== GAME OVER: DRAW ===";
        logLine(msg);

        if (gameOverListener != null) {
            gameOverListener.onGameOver(winner);
        }
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

    // 08-FEB-2026 NEW ADDITION: utility method that converts tiles into pixel coordinates to be passed to Move.java for animation
    public Point.Float tileToPixel(int col, int row) {
        float x = offsetX + col * tileSize + tileSize / 2f;
        float y = offsetY + row * tileSize + tileSize / 2f;
        return new Point.Float(x, y);
    }
    // 08-FEB-2026 NEW ADDITION: utility method that queues a move into moveQueue
    public void enqueueMove(Move move) {
        moveQueue.add(move);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = offsetX + c * tileSize;
                int y = offsetY + r * tileSize;
            g2d.drawImage(tileImages[r][c], x, y, tileSize, tileSize, null);
            }
        }

        g2d.setColor(Color.WHITE);
        // String player = (currentTurn == Unit.Team.ALLY) ? "Player 1" : "Player 2";

        g2d.setColor(Color.BLACK);
        for (int r = 0; r <= rows; r++) {
            int y = offsetY + r * tileSize;
            g2d.drawLine(offsetX, y, offsetX + cols * tileSize, y);
        }
        for (int c = 0; c <= cols; c++) {
            int x = offsetX + c * tileSize;
            g2d.drawLine(x, offsetY, x, offsetY + rows * tileSize);
        }

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

        if (currentMode == ActionMode.MOVE) {
            g2d.setColor(new Color(0, 0, 255, 80)); // blue
        } 
        else if (currentMode == ActionMode.ATTACK) {
            g2d.setColor(new Color(255, 0, 0, 80)); // red
        }
        else if (currentMode == ActionMode.ALLY_TARGET) {
            g2d.setColor(new Color(255, 255, 0, 80)); // 🟡 yellow
        }


        for (Point p : highlights) {
            int x = offsetX + p.x * tileSize;
            int y = offsetY + p.y * tileSize;
            g2d.fillRect(x + 1, y + 1, tileSize - 2, tileSize - 2);
        }

        for (Unit u : units) {
            u.draw(g2d, tileSize, offsetX, offsetY);
            u.drawHealthBar(g2d, tileSize, offsetX, offsetY);
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

       long now = System.currentTimeMillis();

        for (Unit u : units) {
            if (u.isFlashing) {
                long elapsed = now - u.flashStartTime;

                if (elapsed > Unit.FLASH_DURATION) {
                    u.isFlashing = false;
                    u.flashColor = null;
                } else {
                    int x = offsetX + u.getX() * tileSize;
                    int y = offsetY + u.getY() * tileSize;

                    g2d.setColor(u.flashColor);
                    g2d.fillRect(
                        x + 2,
                        y + 2,
                        tileSize - 4,
                        tileSize - 4
                    );
                }
            }
        }


    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void showAllyHighlightsFor(Unit unitXY) {
        List<Point> tiles = new ArrayList<>();

        for (Unit u : units) {
            if (u.team == unitXY.team && u.isAlive()) {
                tiles.add(new Point(u.getX(), u.getY()));
            }
        }

        setHighlights(tiles);
    }

}

/*
    STUFF TO ADD:
    - BFS shortest path for movement / movement animation
    - camera focus effects, animation for world generation 
    - TURN START / PLAYER 2 TURN START / ENEMY TURN START
    - an actually more integrated turn counter because rn it's like lowkey so ass
    - add an option to skip turns for more energy gain
    - skybox. literally just skybox 
    - main menu overhaul so they can do that cutscene thing they want to do
    - maybe make an enemy AI thing
    - original music? how many of these can we even finish in february
    - oh and multiplayer but only if i really really hate myself

    STUFF FOR THE STALL:
    - stickersssss
    - pinsssssssss
    - shirts??????
    - posterssssss
    - biiig poster
*/
