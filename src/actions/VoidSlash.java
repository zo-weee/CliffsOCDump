package actions;

import main.Board;
import units.Unit;

public class VoidSlash implements Action {

    @Override
    public String getName() {
        return "Void Slash";
    }

    @Override
    public int getEnergyCost() {
        return 0;
    }

    @Override
    public boolean canTarget(Board board, Unit user, int col, int row) {
        Unit target = board.getUnit(col, row);
        return target != null &&
               target.team != user.team &&
               Math.abs(user.getX() - col) <= 1 &&
               Math.abs(user.getY() - row) <= 1;
    }

    @Override
    public void execute(Board board, Unit user, int col, int row) {
        Unit target = board.getUnit(col, row);
        if (target == null) return;

        int damage = (int)(user.atk * 1.10);
        board.applyDamage(target, damage);

        user.gainEnergy(2);
    }
}
