package units;

import java.awt.*;

import actions.ForbiddenKnowledge;
import actions.Enlightenment;
import actions.ThePathToSolitude;

public class Buffer extends Unit {

    public Buffer(int x, int y) {
        this.setPosition(x, y);

        this.name = "Genesis Sage";

        this.maxHp = 1700;
        this.curHp = maxHp;
        this.atk = 250;
        this.magicAtk = 300;
        this.def = 270;
        this.energy = 2;
        this.maxEnergy = 6;

        this.moveRange = 2;
        this.attackRange = 1;

        this.actions.add(new ForbiddenKnowledge());
        this.actions.add(new Enlightenment());
        this.actions.add(new ThePathToSolitude());
    }

    @Override
    public void draw(Graphics2D g2d, int tileSize, int offsetX, int offsetY) {
        g2d.setColor(Color.RED);
        g2d.fillOval(
            offsetX + x * tileSize + 10,
            offsetY + y * tileSize + 10,
            tileSize - 20,
            tileSize - 20
        );
    }
}