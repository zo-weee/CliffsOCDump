package units;

import actions.AquaDetention;
import actions.LeviathanWill;
import actions.QueensTorment;
import java.awt.*;
import javax.swing.ImageIcon;

public class Mage extends Unit {

    public Mage(int x, int y) {
        this.setPosition(x, y);

        this.name = "Celestia Presa";
        this.role = "Mage";

        this.maxHp = 1100;
        this.curHp = maxHp;
        this.atk = 60;
        this.magicAtk = 550;
        this.def = 90;
        this.energy = 2;
        this.maxEnergy = 6;

        this.moveRange = 2;
        this.attackRange = 10;

        ImageIcon small = new ImageIcon(getClass().getResource("/assets/Mage/picSmall.png"));
        ImageIcon large = new ImageIcon(getClass().getResource("/assets/Mage/picLarge.png"));

        this.picSelectBG = new ImageIcon(getClass().getResource("/assets/Mage/presabg.png")).getImage();

        this.picSmall = small.getImage().getScaledInstance(
            128, 128, Image.SCALE_SMOOTH
        );

        this.picLarge = large.getImage().getScaledInstance(
            256, 256, Image.SCALE_SMOOTH
        );

        this.flairColor = new Color(204, 255, 153, 255);

        this.basicDesc = 
            "<html><b>Queen's Torment</b>" + "<br>"
            + "Presa opens up a simple spell that summons a hexical strike dealing 80% of her attack to ALL enemies"
        ;

        this.skillDesc =
            "<html><b>Aqua Detention</b>" + "<br>"
            + "Presa goes into a casting state to which she will have her defense reduced to 50% of its original value. On the next turn, Presa will summon the wrath of Leviathan’s roar dealing around 120% of her magic attack to all enemies on the field. When Presa gets attacked during her casting state. The aforementioned spell will NOT trigger." 
        ;

        this.ultimateDesc =
            "<html><b>Leviathan's Will</b>" + "<br>"
            + "Presa goes into a state where she summons Leviathan beside her for two turns. During the next two turns, Presa is able to quickly cast Aqua Detention without the need to enter a casting state. Furthermore, the base damage of Aqua Detention is increased to 150% of her magic attack and Energy now costs [1] instead of [2]." 
        ;

        this.actions.add(new QueensTorment());
        this.actions.add(new AquaDetention());
        this.actions.add(new LeviathanWill());
    }

    @Override
    public void loadWalkSprites() {

        walkFrames = new Image[8];

        String basePath;
        if (team == Team.ALLY) {
            basePath = "/assets/Mage/player1/walk/CP";
        } else {
            basePath = "/assets/Mage/player2/walk/CPE";
        }

        for (int i = 0; i < 8; i++) {
            String wfName = basePath + (i + 1) + ".png";
            walkFrames[i] = new ImageIcon(getClass().getResource(wfName)).getImage();
        }
    }

}