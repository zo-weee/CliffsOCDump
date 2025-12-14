package units;

import java.awt.*;

import javax.swing.ImageIcon;
import actions.VoidSlash;
import actions.ShadowsRise;
import actions.EventHorizon;

public class Fighter extends Unit {

    public Fighter(int x, int y) {
        this.setPosition(x, y);

        this.name = "Rokurou Takahashi";
        this.role = "Fighter";

        this.maxHp = 2400;
        this.curHp = maxHp;
        this.atk = 300;
        this.magicAtk = 50;
        this.def = 450;
        this.energy = 2;
        this.maxEnergy = 4;

        this.moveRange = 2;
        this.attackRange = 1;

        ImageIcon small = new ImageIcon("src/assets/Fighter/picSmall.png");
        ImageIcon large = new ImageIcon("src/assets/Fighter/picLarge.png");

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
        g2d.setColor(Color.BLUE);
        g2d.fillOval(
            offsetX + x * tileSize + 10,
            offsetY + y * tileSize + 10,
            tileSize - 20,
            tileSize - 20
        );
    }
}