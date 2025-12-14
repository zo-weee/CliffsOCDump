package actions;

import main.Board;
import units.Unit;

public class EventHorizon implements Action {
    public String getName() { return "Event Horizon"; }
    public int getEnergyCost() { return 4; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true; 
    }

    public void execute(Board b, Unit u, int c, int r) {
        int dmg = (int)(u.atk * 3.5 + u.magicAtk);
        for (Unit t : b.getUnits())
            if (t.team != u.team)
                b.applyDamage(t, dmg);
    }
}
