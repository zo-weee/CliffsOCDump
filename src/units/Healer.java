package units;

import actions.APromiseToLife;
import actions.LightDrive;
import actions.Sanctification;
import java.awt.*;
import javax.swing.ImageIcon;

public class Healer extends Unit {

    public Healer(int x, int y) {
        this.setPosition(x, y);

        this.name = "Grand Eleanor";
        this.role = "Healer";

        this.maxHp = 2300;
        this.curHp = maxHp;
        this.atk = 140;
        this.magicAtk = 150;
        this.def = 170;
        this.energy = 2;
        this.maxEnergy = 4;

        this.moveRange = 3;
        this.attackRange = 2;

        ImageIcon small = new ImageIcon(getClass().getResource("/assets/Healer/picSmall.png"));
        ImageIcon large = new ImageIcon(getClass().getResource("/assets/Healer/picLarge.png"));
        this.picSmall = small.getImage().getScaledInstance(
            128, 128, Image.SCALE_SMOOTH
        );

        this.picLarge = large.getImage().getScaledInstance(
            256, 256, Image.SCALE_SMOOTH
        );

        this.picSelectBG = new ImageIcon(getClass().getResource("/assets/Healer/eleanorbg.png")).getImage();

        this.flairColor = new Color(255, 128, 0, 255);

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
            basePath = "/assets/Healer/player1/walk/GE";
        } else {
            basePath = "/assets/Healer/player2/walk/GEE";
        }

        for (int i = 0; i < 8; i++) {
            String wfName = basePath + (i + 1) + ".png";
            walkFrames[i] = new ImageIcon(getClass().getResource(wfName)).getImage();
        }
    }

}