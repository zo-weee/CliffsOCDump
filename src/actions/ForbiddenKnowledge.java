package actions;

import main.Board;
import units.Unit;

public class ForbiddenKnowledge implements Action {
    public String getName() { return "Forbidden Knowledge"; }
    public int getEnergyCost() { return 0; }

    public boolean canTarget(Board b, Unit u, int c, int r) {
        return true;
    }

    public TargetType getTargetType() {
        return TargetType.ENEMY;
    }

    public void execute(Board b, Unit u, int c, int r) {

        Unit target = b.getUnit(c, r);
        if (target == null) return;

        int damage = (int)(u.atk * 1) + (int)(u.magicAtk * 1);
        b.applyDamage(target, damage);
        
        u.gainEnergy(2);
    }
}