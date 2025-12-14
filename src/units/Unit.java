package units;

import java.awt.*;
import actions.Action;
import java.util.ArrayList;
import java.util.List;


public abstract class Unit {

    public enum Team {
        ALLY,
        TEAM
    }

    public Team team;

    public int x, y;
    public String name;
    public int curHp, maxHp;
    public int atk, magicAtk;
    public int def;
    public int energy;

    public int moveRange;
    public int attackRange;

    public abstract void draw(Graphics2D g2d, int tileSize, int offsetX, int offsetY);
    public List<Action> actions = new ArrayList<>();

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

    public boolean isAlive() {
        return curHp > 0;
    }

    public void takeDamage(int dmg){
        curHp -= dmg;
        if (curHp < 0) curHp = 0;
    }
}