package actions;

import java.util.ArrayList;
import java.util.List;
import main.Board;
import units.Unit;

public class APromiseToLife implements Action {
    public String getName() { return "A Promise to Life"; }
    public int getEnergyCost() { return 4; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true;
    }


    public void execute(Board b, Unit u, int c, int r) {

        List<Unit> snapshot = new ArrayList<>(b.getUnits());

        for (Unit t : snapshot) {
            if (t != null && t.isAlive() && t.team != u.team) {
                t.curHp += (int)(u.maxHp * 0.40);
            }
        }
    }
}

