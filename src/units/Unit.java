package units;

import actions.Action;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import main.Board;
import status.Status;

public abstract class Unit {

    public enum Team {
        ALLY,
        ENEMY
    }

    public Team team;

    public int x, y;
    public String name;
    public String role;
    public int curHp, maxHp;
    public float hpRatio;
    public float hpRatioShown; // the red HP bar
    public float hpRatioBack; // the grey/green HP bar
    public int atk, magicAtk;
    public int def;
    public int energy, maxEnergy;

    public int moveRange;
    public int attackRange;

    public boolean hasActedThisTurn = false;
    public boolean beingHealed = false;

    public boolean isFlashing = false;
    public Color flashColor = null;
    public long flashStartTime = 0;
    public static final int FLASH_DURATION = 150; // ms

    // 08-FEB-2026 NEW ADDITION: the following variables are for handling unit coordinate position and speed during movement
    // all of them can be overridden based on whether or not we want a unit to move faster or slower in some way
    // but it isn't recommended to make it any slower than the baseline unless they have a short movement range
    // since they move at a fixed speed and all actions are paused while they're moving
    public float pixelX, pixelY;
    public Queue<Point> movePath = null;
    public Point moveFrom, moveTo;
    public float moveProgress = 0f;
    public float moveSpeed = 0.08f;
    public boolean isMoving = false;

    public void triggerFlash(Color color) {
        isFlashing = true;
        flashColor = color;
        flashStartTime = System.currentTimeMillis();
    }
    public List<Action> actions = new ArrayList<>();
    public ArrayList<Status> statuses = new ArrayList<>();

    public int getX() { 
        return x; 
    }

    public int getY() { 
        return y; 
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Image picSmall;
    public Image picLarge;
    public Image picSelectBG;

    public String basicDesc;
    public String skillDesc;
    public String ultimateDesc;

    public Color flairColor;

    public Image[] walkFrames; 
    public int walkFrameIndex = 0;
    public boolean isWalking = false;
    public long lastFrameTime = 0;

    public boolean isAlive() {
        return curHp > 0;
    }

    public void takeDamage(int dmg){
        curHp -= dmg;
        curHp = Math.max(0, Math.min(curHp, maxHp));
        hpRatio = curHp / (float) maxHp;
        hpRatioShown = hpRatio;

        beingHealed = false;
    }
    
    // 21-JAN-2026 NEW ADDITION: this replaces the functionalities of checking for healing in Board.java because that's clunky and stupid;
    // also lets me check healing in Unit.java so it can draw the damn health bar
    public void takeHealing(int heal) {
        curHp += heal;
        if (curHp > maxHp) curHp = maxHp;
        hpRatio = curHp / (float) maxHp;
        hpRatioBack = hpRatio;

        beingHealed = true;
    }

    public boolean hasEnoughEnergy(int cost) {
        return energy >= cost;
    }

    public void spendEnergy(int cost) {
        energy -= cost;
        if (energy < 0) energy = 0;
    }

    public void gainEnergy(int cost) {
        energy += cost;
        if (energy > maxEnergy) energy = maxEnergy;
    }

    public void resetTurn() {
        hasActedThisTurn = false;
    }

    public void addStatus(Status s) {
        statuses.add(s);
    }

    public void processStatuses(Board board) {
        Iterator<Status> it = statuses.iterator();
        while (it.hasNext()) {
            Status s = it.next();
            s.onTurnEnd(board, this);
            if (s.isExpired()) {
                it.remove();
            }
        }
    }

    public boolean hasStatus(Class<?> cls) {
        for (Status s : statuses) {
            if (cls.isInstance(s)) return true;
        }
        return false;
    }

    public void updateAnimation() {
        if (!isWalking || walkFrames == null) return;

        long now = System.currentTimeMillis();
        if (now - lastFrameTime > 120) { // ms per frame
            walkFrameIndex = (walkFrameIndex + 1) % walkFrames.length;
            lastFrameTime = now;
        }
    }

    public Unit createCopy() {
        try {
            return this.getClass()
                    .getConstructor(int.class, int.class)
                    .newInstance(this.x, this.y);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy unit", e);
        }
    }

    // 20-JAN-2026 NEW ADDITION: this code draws the unit's health bar, animation and all; this accounts for healing too
    public void drawHealthBar(Graphics2D hpbar, int tileSize, int offsetX, int offsetY) {

        if (!isAlive() || curHp == maxHp) return;

        if (hpRatioShown == 0f && curHp > 0) {
            hpRatioShown = hpRatio;
        }

        hpRatioShown = Math.max(0f, Math.min(1f, hpRatioShown));

        float speed = 0.01f;
        if (hpRatioShown > hpRatio) {
            hpRatioShown = Math.max(hpRatio, hpRatioShown - speed);
        } else if (hpRatioShown < hpRatio) {
            hpRatioShown = Math.min(hpRatio, hpRatioShown + speed);
        }

        float backSpeed = 0.01f;
        if (hpRatioBack > hpRatio) {
            hpRatioBack = Math.max(hpRatio, hpRatioBack - backSpeed);
        } else {
            hpRatioBack = hpRatio;
        }

        int barHeight = (int) tileSize / 10;
        int x = offsetX + getX() * tileSize;
        int y = offsetY + getY() * tileSize + tileSize - barHeight - 3;

        Color backColor = beingHealed ? Color.GREEN : Color.DARK_GRAY;

        hpbar.setColor(backColor);
        hpbar.fillRect(x + 2, y, (int)((tileSize - 4) * hpRatioBack), barHeight);

        hpbar.setColor(Color.RED);
        hpbar.fillRect(x + 2, y, (int)((tileSize - 4) * hpRatioShown), barHeight);

        if (beingHealed && hpRatioShown >= hpRatio) {
            beingHealed = false;
        }

    }

    public abstract void loadWalkSprites();

    // 08-FEB-2026 NEW ADDITION: defined Unit.draw() because it was literally using the same code across every unit via @Override
    // yall really wanted to make your lives harder
    public void draw(Graphics2D g2d, int tileSize, int offsetX, int offsetY) {
        if (walkFrames == null || walkFrames.length == 0) return;

        // Choose frame: walking animation if moving, otherwise first frame
        Image frame = (isWalking && walkFrames != null)
            ? walkFrames[walkFrameIndex]
            : walkFrames[0];

        int padding = 6;

        // Position to draw: use drawX/drawY if moving, otherwise center on tile
        float drawPosX = isMoving ? pixelX : offsetX + x * tileSize + tileSize / 2f;
        float drawPosY = isMoving ? pixelY : offsetY + y * tileSize + tileSize / 2f;

        int xInt = (int) (drawPosX - tileSize / 2 + padding);
        int yInt = (int) (drawPosY - tileSize / 2 + padding);

        g2d.drawImage(
            frame,
            xInt,
            yInt,
            tileSize - padding * 2,
            tileSize - padding * 2,
            null
        );
    }

}