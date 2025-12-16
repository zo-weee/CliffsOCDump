package actions;

import java.util.ArrayList;
import java.util.List;
import main.Board;
import units.Unit;

public class APromiseToLife implements Action {
    public String getName() { return "A Promise to Life"; }
    public int getEnergyCost() { return 4; }

    @Override
    public boolean isSupport() { return true; } // ✅ heal

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true; // global-ish
    }

    public void execute(Board b, Unit u, int c, int r) {
        List<Unit> snapshot = new ArrayList<>(b.getUnits());

        int heal = (int)(u.maxHp * 0.40);

        for (Unit t : snapshot) {
            if (t != null && t.isAlive() && t.team == u.team) { // ✅ was !=
                b.applyHeal(t, heal); // ✅ clamps + logs
            }
        }
    }
}
