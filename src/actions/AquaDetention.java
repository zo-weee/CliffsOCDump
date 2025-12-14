package actions;

import main.Board;
import units.Unit;
import status.DelayedSpellStatus;
import status.LeviathanWillStatus;

public class AquaDetention implements Action {
    public String getName() { return "Aqua Detention"; }
    public int getEnergyCost() { return 2; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true;
    }

    @Override
    public void execute(Board b, Unit u, int c, int r) {

        boolean empowered = u.hasStatus(LeviathanWillStatus.class);

        if (empowered) {
            // INSTANT, stronger version
            for (Unit t : b.getUnits()) {
                if (t.team != u.team) {
                    b.applyDamage(t, (int)(u.magicAtk * 1.5));
                }
            }
        } else {
            // NORMAL delayed version
            u.def /= 2;

            u.addStatus(new DelayedSpellStatus(1, () -> {
                for (Unit t : b.getUnits()) {
                    if (t.team != u.team) {
                        b.applyDamage(t, (int)(u.magicAtk * 1.2));
                    }
                }
                u.def *= 2;
            }));
        }
    }

}

