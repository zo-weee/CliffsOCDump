package actions;

import java.util.ArrayList;
import java.util.List;
import main.Board;
import main.Board.TurnPhase;
import status.LeviathanWillStatus;
import units.Unit;

public class AquaDetention implements Action {
    public String getName() { return "Aqua Detention"; }
    public int getEnergyCost() { return 2; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true;
    }

    public TargetType getTargetType() {
        return TargetType.ENEMY;
    }

    @Override
    public void execute(Board b, Unit u, int c, int r) {

        boolean empowered = u.hasStatus(LeviathanWillStatus.class);

        // 14-JAN-2026: reformatted AquaDetention to use the new ScheduledAction features;
        // original code commented out jic it literally doesn't work
        // if it works, we can delete DelayedSpellStatus - it's no longer being used

        if (empowered) {
            List<Unit> snapshot = new ArrayList<>(b.getUnits());

            for (Unit t : snapshot) {
                if (t != null && t.isAlive() && t.team != u.team) {
                    b.applyDamage(t, (int)(u.magicAtk * 2.7));
                }
            }
        } else {
        /*
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
            */
            u.def /= 2;
            b.scheduleAction(3, TurnPhase.END, board -> {
                List<Unit> snapshot = new ArrayList<>(b.getUnits());
                for (Unit t : snapshot) {
                    if (t != null && t.isAlive() && t.team != u.team) {
                        b.applyDamage(t, (int)(u.magicAtk * 1.2));
                    }
                }
                u.def *= 2;
            });
        }
    }

}

