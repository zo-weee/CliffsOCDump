package actions;

import main.Board;
import units.Unit;

public class BasicAttack implements Action {

    @Override
    public String getName() {
        return "Basic Attack";
    }

    @Override
    public int getEnergyCost() {
        return 0;
    }

    @Override
    public boolean canTarget(Board board, Unit user, int col, int row) {
        Unit target = board.getUnit(col, row);
        return target != null && target.team != user.team;
    }

    @Override
    public void execute(Board board, Unit user, int col, int row) {
        Unit target = board.getUnit(col, row);
        if (target == null) return;

        int rawDamage = user.atk;
        board.applyDamage(target, rawDamage);
    }
}
