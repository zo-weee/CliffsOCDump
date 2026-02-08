package main;

import java.awt.Point;
import units.Unit;

public class Move {

    private final Unit unit;
    private final java.util.Queue<Point> path;
    private final Board board;

    private Point currentTarget; // the next tile we're moving toward
    private float progress = 0f; // 0 → 1 interpolation between tiles
    private final float speed = 4.0f; // tiles per second

    public boolean finished = false;

    public Move(Board board, Unit unit) {
        this.board = board;
        this.unit = unit;
        this.path = unit.movePath;

        unit.isMoving = true;
        advanceToNextTile();
    }

    private void advanceToNextTile() {
        if (path.isEmpty()) {
            finished = true;
            unit.isMoving = false;
            return;
        }
        currentTarget = path.poll();
        progress = 0f;
    }

    public void update(float delta) {
        if (finished || currentTarget == null) {
            unit.isWalking = false;
            return;
        }

        unit.isWalking = true;

        float startX = board.offsetX + unit.getX() * board.tileSize + board.tileSize / 2f;
        float startY = board.offsetY + unit.getY() * board.tileSize + board.tileSize / 2f;

        float targetX = board.offsetX + currentTarget.x * board.tileSize + board.tileSize / 2f;
        float targetY = board.offsetY + currentTarget.y * board.tileSize + board.tileSize / 2f;

        // Interpolate progress
        progress += delta * speed;

        if (progress >= 1f) {
            // Snap to target tile
            unit.pixelX = targetX;
            unit.pixelY = targetY;
            unit.setPosition(currentTarget.x, currentTarget.y);

            // Move to the next tile
            advanceToNextTile();
        } else {
            // Linear interpolation
            unit.pixelX = startX + (targetX - startX) * progress;
            unit.pixelY = startY + (targetY - startY) * progress;
        }

        if (finished) {
            unit.isWalking = false;
            unit.walkFrameIndex = 0;
        }
    }
}