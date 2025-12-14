package actions;

import main.Board;
import units.Unit;

public class Sanctification implements Action {
    public String getName() { return "Sanctification"; }
    public int getEnergyCost() { return 1; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        Unit t = b.getUnit(c, r);
        return t != null && t.team == u.team;
    }

    public void execute(Board b, Unit u, int c, int r) {
        Unit t = b.getUnit(c, r);
        int heal = (int)(u.maxHp * 0.2) + 54;
        t.curHp = Math.min(t.maxHp, t.curHp + heal);
    }
}
