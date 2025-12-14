package actions;

import main.Board;
import units.Unit;

public class ThePathToSolitude implements Action {
    public String getName() { return "The Path to Solitude"; }
    public int getEnergyCost() { return 4; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        Unit t = b.getUnit(c, r);
        return t != null && t.team == u.team;
    }

    public void execute(Board b, Unit u, int c, int r) {
        Unit t = b.getUnit(c, r);
        t.energy += 4;
        if (t.energy > t.maxEnergy){
            t.energy = t.maxEnergy;
        }
    }
}

