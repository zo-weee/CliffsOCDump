package units;

import actions.AbsoluteTyranny;
import actions.DevilsMaw;
import actions.LoneWolfCharge;
import java.awt.*;
import javax.swing.ImageIcon;

public class Tank extends Unit {

    public Tank(int x, int y) {
        this.setPosition(x, y);

        this.name = "Cerberus";
        this.role = "Tank";

        this.maxHp = 1850;
        this.curHp = maxHp;
        this.atk = 150;
        this.magicAtk = 40;
        this.def = 290;
        this.energy = 2;
        this.maxEnergy = 5;

        this.moveRange = 3;
        this.attackRange = 10;

        ImageIcon small = new ImageIcon(getClass().getResource("/assets/Tank/picSmall.png"));
        ImageIcon large = new ImageIcon(getClass().getResource("/assets/Tank/picLarge.png"));

        this.picSelectBG = new ImageIcon(getClass().getResource("/assets/Tank/cerberusbg.png")).getImage();

        this.picSmall = small.getImage().getScaledInstance(
            128, 128, Image.SCALE_SMOOTH
        );

        this.picLarge = large.getImage().getScaledInstance(
            256, 256, Image.SCALE_SMOOTH
        );

        this.flairColor = new Color(252, 198, 50, 255);

        this.basicDesc = 
            "<html><b>Lone Wolf Charge</b>" + "<br>"
            + "Cerberus summons a wolf to charge upon an enemy dealing around 100% of his attack plus 120% of his magic attack to all enemies."
        ;

        this.skillDesc =
            "<html><b>Devil's Maw</b>" + "<br>"
            + "Cerberus deals damage equal to 50% of his attack to all enemies and increases his defense to 130% of its original value." 
        ;

        this.ultimateDesc =
            "<html><b>Absolute Tyranny</b>" + "<br>"
            + "Cerberus increases all allies' defenses equal to 60% + 68 of his defense." 
        ;

        this.actions.add(new LoneWolfCharge());
        this.actions.add(new DevilsMaw());
        this.actions.add(new AbsoluteTyranny());
    }

    @Override
    public void draw(Graphics2D g2d, int tileSize, int offsetX, int offsetY) {

        Image frame = (isWalking && walkFrames != null)
            ? walkFrames[walkFrameIndex]
            : walkFrames[0];

        int padding = 6;

        g2d.drawImage(
            frame,
            offsetX + x * tileSize + padding,
            offsetY + y * tileSize + padding,
            tileSize - padding * 2,
            tileSize - padding * 2,
            null
        );
    }

    @Override
    public void loadWalkSprites() {

        walkFrames = new Image[5];

        String basePath;
        if (team == Team.ALLY) {
            basePath = "/assets/Tank/player1/walk/C";
        } else {
            basePath = "/assets/Tank/player2/walk/CE";
        }

        for (int i = 0; i < 5; i++) {
            String wfName = basePath + (i + 1) + ".png";
            walkFrames[i] = new ImageIcon(getClass().getResource(wfName)).getImage();
        }
    }

}