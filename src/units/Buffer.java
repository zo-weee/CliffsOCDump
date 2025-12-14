package units;

import java.awt.*;
import javax.swing.ImageIcon;

import actions.ForbiddenKnowledge;
import actions.Enlightenment;
import actions.ThePathToSolitude;

public class Buffer extends Unit {

    public Buffer(int x, int y) {
        this.setPosition(x, y);

        this.name = "Genesis Sage";
        this.role = "Buffer";

        this.maxHp = 1700;
        this.curHp = maxHp;
        this.atk = 250;
        this.magicAtk = 300;
        this.def = 270;
        this.energy = 2;
        this.maxEnergy = 6;

        this.moveRange = 2;
        this.attackRange = 1;

        ImageIcon small = new ImageIcon("src/assets/Buffer/picSmall.png");
        ImageIcon large = new ImageIcon("src/assets/Buffer/picLarge.png");

        this.picSmall = small.getImage().getScaledInstance(
            128, 128, Image.SCALE_SMOOTH
        );

        this.picLarge = large.getImage().getScaledInstance(
            256, 256, Image.SCALE_SMOOTH
        );

        // === SKILL DESCRIPTIONS (UI) ===
        this.basicDesc =
            "<html><b>Forbidden Knowledge</b><br>"
            + "Genesis Sage recalls the falsifying prophecy and redirects it to one designated enemy "
            + "dealing 60% of his attack plus 60% of his magic attack.";

        this.skillDesc =
            "<html><b>Enlightenment</b><br>"
            + "Genesis harnesses the world’s truth and guides it to one designated ally. "
            + "If the target ally is a fighter, increase attack by 200% of Genesis's attack. "
            + "If the target ally is a buffer, increase speed to 30% of its original value. "
            + "If the character is a mage, increase magic attack by 140%. "
            + "If the character is a healer, increase Max HP by 900.";

        this.ultimateDesc =
            "<html><b>The Path to Solitude</b><br>"
            + "Genesis grants one ally 4 points of energy.";

        // === SKILL LOGIC (GAMEPLAY) ===
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
