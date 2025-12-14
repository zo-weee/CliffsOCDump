package units;

import java.awt.*;

import javax.swing.ImageIcon;
import actions.QueensTorment;
import actions.AquaDetention;
import actions.LeviathanWill;

public class Mage extends Unit {

    public Mage(int x, int y) {
        this.setPosition(x, y);

        this.name = "Celestia Presa";
        this.role = "Mage";

        this.maxHp = 1200;
        this.curHp = maxHp;
        this.atk = 60;
        this.magicAtk = 650;
        this.energy = 2;
        this.maxEnergy = 6;

        this.moveRange = 2;
        this.attackRange = 2;

        ImageIcon small = new ImageIcon("src/assets/Mage/picSmall.png");
        ImageIcon large = new ImageIcon("src/assets/Mage/picLarge.png");

        this.picSmall = small.getImage().getScaledInstance(
            128, 128, Image.SCALE_SMOOTH
        );

        this.picLarge = large.getImage().getScaledInstance(
            256, 256, Image.SCALE_SMOOTH
        );

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
            + "Rokorou summons the destructiveness of a black hole dealing 350% of his attack + 100% of his magic attack to all enemies on the battlefield" 
        ;
        
        this.actions.add(new QueensTorment());
        this.actions.add(new AquaDetention());
        this.actions.add(new LeviathanWill());
    }

    @Override
    public void draw(Graphics2D g2d, int tileSize, int offsetX, int offsetY) {
        g2d.setColor(Color.PINK);
        g2d.fillOval(
            offsetX + x * tileSize + 10,
            offsetY + y * tileSize + 10,
            tileSize - 20,
            tileSize - 20
        );
    }
}