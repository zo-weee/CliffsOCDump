package actions;

import java.util.ArrayList;
import java.util.List;
import main.Board;
import units.Unit;

public class QueensTorment implements Action {
    public String getName() { return "Queen's Torment"; }
    public int getEnergyCost() { return 0; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true;
    }

    public void execute(Board b, Unit u, int c, int r) {
        List<Unit> snapshot = new ArrayList<>(b.getUnits());

        for (Unit t : snapshot) {
            if (t.isAlive() && t.team != u.team) {
                b.applyDamage(t, (int)(u.magicAtk * 0.8));
            }
        }  
        u.gainEnergy(2);
    }
}

