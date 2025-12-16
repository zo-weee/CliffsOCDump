package actions;

import main.Board;
import units.Unit;

public class LightDrive implements Action {

    @Override
    public String getName() {
        return "Light Drive";
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
               Math.abs(user.getX() - col) <= user.attackRange &&
               Math.abs(user.getY() - row) <= user.attackRange;
    }

    public TargetType getTargetType() {
        return TargetType.ENEMY;
    }

    @Override
    public void execute(Board board, Unit user, int col, int row) {
        Unit target = board.getUnit(col, row);
        if (target == null) return;

        int damage = (int)(user.atk * 0.8) + (int)(user.magicAtk * 1.1);
        board.applyDamage(target, damage);

        user.gainEnergy(2);
    }
}
