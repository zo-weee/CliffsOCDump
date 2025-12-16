package actions;

import java.util.ArrayList;
import java.util.List;
import main.Board;
import units.Unit;

public class DevilsMaw implements Action {
    public String getName() { return "Devil's Maw"; }
    public int getEnergyCost() { return 1; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true;
    }

    public TargetType getTargetType() {
        return TargetType.ENEMY;
    }

    public void execute(Board b, Unit u, int c, int r) {
        List<Unit> snapshot = new ArrayList<>(b.getUnits());

        for (Unit t : snapshot) {
            if (t != null && t.isAlive() && t.team != u.team) {
                b.applyDamage(t, (int)(u.atk * 1) + (int)(u.magicAtk * 0.8));
            }
        }

        u.def = (int)(u.def * 1.3);
    }
}

