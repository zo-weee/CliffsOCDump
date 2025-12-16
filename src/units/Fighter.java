package units;

import actions.EventHorizon;
import actions.ShadowsRise;
import actions.VoidSlash;
import java.awt.*;
import javax.swing.ImageIcon;

public class Fighter extends Unit {

    public Fighter(int x, int y) {
        this.setPosition(x, y);

        this.name = "Rokurou Takahashi";
        this.role = "Fighter";

        this.maxHp = 1700;
        this.curHp = maxHp;
        this.atk = 275;
        this.magicAtk = 50;
        this.def = 150;
        this.energy = 2;
        this.maxEnergy = 4;

        this.moveRange = 5;
        this.attackRange = 2;

        ImageIcon small = new ImageIcon(getClass().getResource("/assets/Fighter/picSmall.png"));
        ImageIcon large = new ImageIcon(getClass().getResource("/assets/Fighter/picLarge.png"));

        this.picSmall = small.getImage().getScaledInstance(
            128, 128, Image.SCALE_SMOOTH
        );

        this.picLarge = large.getImage().getScaledInstance(
            256, 256, Image.SCALE_SMOOTH
        );

        this.basicDesc = 
            "<html><b>Void Slash</b>" + "<br>"
            + "Rokorou dashes and cuts through a SINGLE enemy dealing 110% of his current attack"
        ;

        this.skillDesc =
            "<html><b>Shadows Rise</b>" + "<br>"
            + "Rokorou garners the might of celestial nothingness to quickly flash forward and deal damage equal to 250% of his attack to an ENEMY and their ADJACENT UNITS." 
        ;
        this.ultimateDesc =
            "<html><b>Event Horizon</b>" + "<br>"
            + "Rokorou summons the destructiveness of a black hole dealing 350% of his attack + 100% of his magic attack to all enemies on the battlefield" 
        ;

        this.actions.add(new VoidSlash());
        this.actions.add(new ShadowsRise());
        this.actions.add(new EventHorizon());
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

        walkFrames = new Image[8];

        String basePath;
        if (team == Team.ALLY) {
            basePath = "/assets/Fighter/player1/walk/RK";
        } else {
            basePath = "/assets/Fighter/player2/walk/RKE";
        }

        for (int i = 0; i < 8; i++) {
            String wfName = basePath + (i + 1) + ".png";
            walkFrames[i] = new ImageIcon(getClass().getResource(wfName)).getImage();
        }
    }

    
}