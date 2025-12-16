package actions;

import java.util.ArrayList;
import java.util.List;
import main.Board;
import units.Unit;

public class EventHorizon implements Action {
    public String getName() { return "Event Horizon"; }
    public int getEnergyCost() { return 4; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true; 
    }

    public TargetType getTargetType() {
        return TargetType.ENEMY;
    }

    public void execute(Board b, Unit u, int c, int r) {
        int dmg = (int)(u.atk * 2.5 + u.magicAtk);

        List<Unit> snapshot = new ArrayList<>(b.getUnits());

        for (Unit t : snapshot) {
            if (t.isAlive() && t.team != u.team) {
                b.applyDamage(t, dmg);
            }
        }
    }

}
