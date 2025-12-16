package actions;

import main.Board;
import units.Unit;
import status.LeviathanWillStatus;

public class LeviathanWill implements Action {

    @Override
    public String getName() {
        return "Leviathan's Will";
    }

    @Override
    public int getEnergyCost() {
        return 5;
    }

    @Override
    public boolean canTarget(Board b, Unit u, int c, int r) {
        return false;
    }

    public TargetType getTargetType() {
        return TargetType.SELF;
    }

    @Override
    public void execute(Board b, Unit u, int c, int r) {

        if (u.hasStatus(LeviathanWillStatus.class)) return;

        u.addStatus(new LeviathanWillStatus(4));
    }
}
