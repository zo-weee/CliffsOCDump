package actions;

import java.util.ArrayList;
import java.util.List;
import main.Board;
import status.DelayedSpellStatus;
import status.LeviathanWillStatus;
import units.Unit;

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
            List<Unit> snapshot = new ArrayList<>(b.getUnits());

            for (Unit t : snapshot) {
                if (t != null && t.isAlive() && t.team != u.team) {
                    b.applyDamage(t, (int)(u.magicAtk * 1.5));
                }
            }
        } else {
            u.def /= 2;

            u.addStatus(new DelayedSpellStatus(3, () -> {
                List<Unit> snapshot = new ArrayList<>(b.getUnits());
                for (Unit t : snapshot) {
                    if (t != null && t.isAlive() && t.team != u.team) {
                        b.applyDamage(t, (int)(u.magicAtk * 1.2));
                    }
                }
                u.def *= 2;
            }));
        }
    }

}

