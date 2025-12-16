package actions;

import main.Board;
import units.Unit;

public class Sanctification implements Action {
    public String getName() { return "Sanctification"; }
    public int getEnergyCost() { return 1; }
    
    @Override
    public boolean isSupport() { return true; }


    public boolean canTarget(Board b, Unit u, int c, int r) {
        Unit t = b.getUnit(c, r);
        return t != null && t.team == u.team;
    }

    public void execute(Board b, Unit u, int c, int r) {
        Unit t = b.getUnit(c, r);
        if (t == null) return;

        int heal = (int)(u.maxHp * 0.2) + 54;
        b.applyHeal(t, heal);
    }

}
