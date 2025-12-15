package actions;

import java.util.ArrayList;
import java.util.List;
import main.Board;
import units.Unit;

public class AbsoluteTyranny implements Action {
    public String getName() { return "Absolute Tyranny"; }
    public int getEnergyCost() { return 3; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true;
    }

    public void execute(Board b, Unit u, int c, int r) {
        List<Unit> snapshot = new ArrayList<>(b.getUnits());

        for (Unit t : snapshot) {
            if (t != null && t.isAlive() && t.team != u.team) {
                t.def += 68 + (int)(u.def * 0.6);
            }
        }
    }
}

