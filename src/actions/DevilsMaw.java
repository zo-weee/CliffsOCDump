package actions;

import main.Board;
import units.Unit;

public class DevilsMaw implements Action {
    public String getName() { return "Devil's Maw"; }
    public int getEnergyCost() { return 1; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true;
    }

    public void execute(Board b, Unit u, int c, int r) {
        for (Unit t : b.getUnits())
            if (t.team != u.team)
                b.applyDamage(t, (int)(u.atk * 0.5));
        u.def = (int)(u.def * 1.3);
    }
}

