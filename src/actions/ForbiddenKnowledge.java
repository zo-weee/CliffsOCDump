package actions;

import main.Board;
import units.Unit;

public class ForbiddenKnowledge implements Action {
    public String getName() { return "Queen's Torment"; }
    public int getEnergyCost() { return 0; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true;
    }

    public void execute(Board b, Unit u, int c, int r) {
        for (Unit t : b.getUnits())
            if (t.team != u.team)
                b.applyDamage(t, (int)(u.atk * 0.6) + (int)(u.magicAtk * 0.6));
        u.gainEnergy(2);
    }
}