package units;

import java.awt.*;

import actions.BasicAttack;

public class Fighter extends Unit {

    public Fighter(int x, int y) {
        this.setPosition(x, y);

        this.name = "Rokurou Takahashi";

        this.maxHp = 2400;
        this.curHp = maxHp;
        this.atk = 300;
        this.magicAtk = 50;
        this.def = 450;
        this.energy = 4;

        this.moveRange = 2;
        this.attackRange = 1;

        this.actions.add(new BasicAttack());
    }

    @Override
    public void draw(Graphics2D g2d, int tileSize, int offsetX, int offsetY) {
        g2d.setColor(Color.BLUE);
        g2d.fillOval(
            offsetX + x * tileSize + 10,
            offsetY + y * tileSize + 10,
            tileSize - 20,
            tileSize - 20
        );
    }
}