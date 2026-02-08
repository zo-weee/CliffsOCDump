package units;

import actions.Enlightenment;
import actions.ForbiddenKnowledge;
import actions.ThePathToSolitude;
import java.awt.*;
import javax.swing.ImageIcon;

public class Buffer extends Unit {

    public Buffer(int x, int y) {
        this.setPosition(x, y);

        this.name = "Genesis Sage";
        this.role = "Buffer";

        this.maxHp = 1300;
        this.curHp = maxHp;
        this.atk = 100;
        this.magicAtk = 210;
        this.def = 130;
        this.energy = 2;
        this.maxEnergy = 6;

        this.moveRange = 2;
        this.attackRange = 10;

        ImageIcon small = new ImageIcon(getClass().getResource("/assets/Buffer/picSmall.png"));
        ImageIcon large = new ImageIcon(getClass().getResource("/assets/Buffer/picLarge.png"));

        this.picSelectBG = new ImageIcon(getClass().getResource("/assets/Buffer/sagebg.png")).getImage();

        this.picSmall = small.getImage().getScaledInstance(
            128, 128, Image.SCALE_SMOOTH
        );

        this.picLarge = large.getImage().getScaledInstance(
            256, 256, Image.SCALE_SMOOTH
        );

        this.flairColor = new Color(168, 210, 255, 8);

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

        this.actions.add(new ForbiddenKnowledge());
        this.actions.add(new Enlightenment());
        this.actions.add(new ThePathToSolitude());
    }

    @Override
    public void loadWalkSprites() {

        walkFrames = new Image[8];

        String basePath;
        if (team == Team.ALLY) {
            basePath = "/assets/Buffer/player1/walk/GS";
        } else {
            basePath = "/assets/Buffer/player2/walk/GSE";
        }

        for (int i = 0; i < 8; i++) {
            String wfName = basePath + (i + 1) + ".png";
            walkFrames[i] = new ImageIcon(getClass().getResource(wfName)).getImage();
        }
    }

}
