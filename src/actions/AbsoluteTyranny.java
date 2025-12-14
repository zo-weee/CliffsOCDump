package actions;

import main.Board;
import units.Unit;

public class AbsoluteTyranny implements Action {
    public String getName() { return "Queen's Torment"; }
    public int getEnergyCost() { return 3; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true;
    }

    public void execute(Board b, Unit u, int c, int r) {
        for (Unit t : b.getUnits())
            if (t.team == u.team) {
                t.def += 68 + (int)(u.def * 0.6);
            }
    }
}

