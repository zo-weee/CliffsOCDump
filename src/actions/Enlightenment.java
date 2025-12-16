package actions;

import main.Board;
import units.Buffer;
import units.Fighter;
import units.Healer;
import units.Mage;
import units.Tank;
import units.Unit;

public class Enlightenment implements Action {
    public String getName() { return "Enlightenment"; }
    public int getEnergyCost() { return 3; }
    
    @Override
    public boolean isSupport() { return true; }


    public boolean canTarget(Board b, Unit u, int c, int r) {
        Unit t = b.getUnit(c, r);
        return t != null && t.team == u.team;
    }

    public void execute(Board b, Unit u, int c, int r) {
        Unit t = b.getUnit(c, r);
        if (t instanceof Fighter)
            t.atk += u.atk * 2;
        else if (t instanceof Mage)
            t.magicAtk = (int)(t.magicAtk * 1.4);
        else if (t instanceof Buffer)
            t.energy += 4;
        else if (t instanceof Healer)
            t.maxHp += 900;
        else if (t instanceof Tank)
            t.magicAtk = (int)(t.def * 1.8);
    }
}

