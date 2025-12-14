package units;

import java.awt.*;

import actions.QueensTorment;
import actions.AquaDetention;
import actions.LeviathanWill;

public class Mage extends Unit {

    public Mage(int x, int y) {
        this.setPosition(x, y);

        this.name = "Celestia Presa";

        this.maxHp = 1200;
        this.curHp = maxHp;
        this.atk = 60;
        this.magicAtk = 650;
        this.energy = 2;
        this.maxEnergy = 6;

        this.moveRange = 2;
        this.attackRange = 2;

        this.actions.add(new QueensTorment());
        this.actions.add(new AquaDetention());
        this.actions.add(new LeviathanWill());
    }

    @Override
    public void draw(Graphics2D g2d, int tileSize, int offsetX, int offsetY) {
        g2d.setColor(Color.PINK);
        g2d.fillOval(
            offsetX + x * tileSize + 10,
            offsetY + y * tileSize + 10,
            tileSize - 20,
            tileSize - 20
        );
    }
}