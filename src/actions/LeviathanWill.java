package actions;

import main.Board;
import status.LeviathanWillStatus;
import units.Unit;

public class LeviathanWill implements Action {

    public String getName() { return "Leviathan's Will"; }
    public int getEnergyCost() { return 5; }

    @Override
    public boolean isSupport() { return true; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        // ✅ only allow casting if not already active
        return !u.hasStatus(LeviathanWillStatus.class);
    }

    public void execute(Board b, Unit u, int c, int r) {
        if (u.hasStatus(LeviathanWillStatus.class)) return;
        u.addStatus(new LeviathanWillStatus(2));
    }
}
