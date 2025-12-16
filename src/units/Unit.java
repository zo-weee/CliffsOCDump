package units;

import actions.Action;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    public int atk, magicAtk;
    public int def;
    public int energy, maxEnergy;

    public int moveRange;
    public int attackRange;

    public boolean hasActedThisTurn = false;

    public abstract void draw(Graphics2D g2d, int tileSize, int offsetX, int offsetY);
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
        if (curHp < 0) curHp = 0;
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

    public abstract void loadWalkSprites();

}