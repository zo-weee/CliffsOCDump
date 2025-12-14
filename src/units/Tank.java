package units;

import java.awt.*;

import actions.LoneWolfCharge;
import actions.DevilsMaw;
import actions.AbsoluteTyranny;

public class Tank extends Unit {

    public Tank(int x, int y) {
        this.setPosition(x, y);

        this.name = "Cerberus";

        this.maxHp = 3700;
        this.curHp = maxHp;
        this.atk = 120;
        this.magicAtk = 20;
        this.def = 540;
        this.energy = 2;
        this.maxEnergy = 5;

        this.moveRange = 1;
        this.attackRange = 1;

        this.actions.add(new LoneWolfCharge());
        this.actions.add(new DevilsMaw());
        this.actions.add(new AbsoluteTyranny());
    }

    @Override
    public void draw(Graphics2D g2d, int tileSize, int offsetX, int offsetY) {
        g2d.setColor(Color.ORANGE);
        g2d.fillOval(
            offsetX + x * tileSize + 10,
            offsetY + y * tileSize + 10,
            tileSize - 20,
            tileSize - 20
        );
    }
}