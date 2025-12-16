package actions;

import main.Board;
import units.Unit;

public class ShadowsRise implements Action {

    @Override
    public String getName() {
        return "Shadows Rise";
    }

    @Override
    public int getEnergyCost() {
        return 1;
    }

    @Override
    public boolean canTarget(Board board, Unit user, int col, int row) {
        Unit target = board.getUnit(col, row);
        return target != null &&
               target.team != user.team &&
               Math.abs(user.getX() - col) <= user.attackRange &&
               Math.abs(user.getY() - row) <= user.attackRange;
    }

    @Override
    public void execute(Board board, Unit user, int col, int row) {

        int damage = (int)(user.atk * 2.5);

        // 3x3 AoE centered on target
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {

                int tx = col + dx;
                int ty = row + dy;

                Unit target = board.getUnit(tx, ty);
                if (target == null) continue;
                if (target.team == user.team) continue;

                board.applyDamage(target, damage);
            }
        }
    }
}
