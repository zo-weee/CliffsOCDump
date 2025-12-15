package units;

import java.awt.*;

import javax.swing.ImageIcon;
import actions.LightDrive;
import actions.Sanctification;
import actions.APromiseToLife;

public class Healer extends Unit {

    public Healer(int x, int y) {
        this.setPosition(x, y);

        this.name = "Grand Eleanor";
        this.role = "Healer";

        this.maxHp = 2200;
        this.curHp = maxHp;
        this.atk = 130;
        this.magicAtk = 200;
        this.def = 540;
        this.energy = 2;
        this.maxEnergy = 4;

        this.moveRange = 2;
        this.attackRange = 1;

        ImageIcon small = new ImageIcon("src/assets/Healer/picSmall.png");
        ImageIcon large = new ImageIcon("src/assets/Healer/picLarge.png");

        this.picSmall = small.getImage().getScaledInstance(
            128, 128, Image.SCALE_SMOOTH
        );

        this.picLarge = large.getImage().getScaledInstance(
            256, 256, Image.SCALE_SMOOTH
        );

        this.basicDesc = 
            "<html><b>Light Drive</b>" + "<br>"
            + "Eleanor slashes her spear to one enemy dealing 130% of her attack plus 20% of her magic attack."
        ;

        this.skillDesc =
            "<html><b>Sanctification</b>" + "<br>"
            + "Eleanor says a magic chant calling upon all seraphim’s blessings. She designates their gifts into one ally, healing them of around 20% + 54 of Eleanor’s Max HP."
        ;

        this.ultimateDesc =
            "<html><b>A Promise to Life</b>" + "<br>"
            + "Eleanor heals all allies an amount equal to 40% of Eleanor’s Max HP." 
        ;

        this.actions.add(new LightDrive());
        this.actions.add(new Sanctification());
        this.actions.add(new APromiseToLife());
        
        walkFrames = new Image[8];

        for (int i = 0; i < 8; i++) {
            walkFrames[i] = new ImageIcon(
                "src/assets/Healer/walk/GE" + (i + 1) + ".png"
            ).getImage();
        }
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
}